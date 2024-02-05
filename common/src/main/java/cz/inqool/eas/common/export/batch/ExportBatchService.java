package cz.inqool.eas.common.export.batch;

import cz.inqool.eas.common.authored.AuthoredService;
import cz.inqool.eas.common.exception.GeneralException;
import cz.inqool.eas.common.export.event.ExportBatchProcessedEvent;
import cz.inqool.eas.common.export.event.ExportFinishedEvent;
import cz.inqool.eas.common.export.request.ExportRequest;
import cz.inqool.eas.common.export.request.ExportRequestState;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.storage.file.OpenedFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ExportBatchService extends AuthoredService<
        ExportBatch,
        ExportBatchDetail,
        ExportBatchList,
        ExportBatchCreate,
        ExportBatchUpdate,
        ExportBatchRepository
        > {

    private FileManager fileManager;

    @Override
    protected void preCreateHook(ExportBatch object) {
        super.preCreateHook(object);

        object.setState(ExportBatchState.PROCESSING);
    }

    @Async
    @EventListener
    public synchronized void handleExportFinishedEvent(ExportFinishedEvent event) {
        ExportRequest request = event.getPayload();

        ExportBatch batch = repository.findByRequest(request);

        if (batch != null) {
            Set<ExportRequest> requests = batch.getRequests();

            boolean finished = requests
                    .stream()
                    .map(ExportRequest::getState)
                    .allMatch(state -> state == ExportRequestState.FAILED || state == ExportRequestState.PROCESSED);

            if (finished) {
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

                ExportBatch finalizingBatch = transactionTemplate.execute(status -> {
                    batch.setState(ExportBatchState.FINALIZING);
                    return repository.update(batch);
                });

                if (finalizingBatch != null) {
                    File file = packResult(finalizingBatch);

                    ExportBatch processedBatch = transactionTemplate.execute(status -> {
                        finalizingBatch.setResult(file);
                        finalizingBatch.setState(ExportBatchState.PROCESSED);
                        return repository.update(finalizingBatch);
                    });

                    eventPublisher.publishEvent(new ExportBatchProcessedEvent(this, processedBatch));
                }
            }
        }
    }

    public File packResult(ExportBatch batch) {
        Set<ExportRequest> requests = batch.getRequests();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (ZipOutputStream zipOut = new ZipOutputStream(bos)) {
            for (ExportRequest request : requests) {
                File requestResult = request.getResult();

                if (requestResult != null) {
                    OpenedFile openedFile = fileManager.open(requestResult.getId());

                    try (InputStream requestResultStream = openedFile.getStream()) {
                        ZipEntry zipEntry = new ZipEntry(requestResult.getName());
                        zipOut.putNextEntry(zipEntry);

                        requestResultStream.transferTo(zipOut);

                        zipOut.closeEntry();
                    } catch (IOException e) {
                        log.error("Error writing {} result to {}.", request, batch);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error during {} packaging.", batch);
            throw new GeneralException(e);
        }

        byte[] data = bos.toByteArray();
        return fileManager.store(batch.getName() + ".zip", data.length, "application/zip", new ByteArrayInputStream(data));
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }
}
