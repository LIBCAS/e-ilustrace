package cz.inqool.eas.eil.download;

import cz.inqool.eas.common.exception.v2.InvalidArgument;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.eil.record.link.Link;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.RecordRepository;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.illustration.Illustration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static cz.inqool.eas.eil.config.exception.EilExceptionCode.UNRECOGNIZED_LINK_DESCRIPTION;

/**
 * Handles download of files (scans of illustrations, illustration pages and book front pages) from links specified in
 * datafields 856 and 998.
 * First, DownloadReference is created from parsed Link instance and saved to DB.
 * Later, existing download references are processed via cron job.
 */
@Service
@Slf4j
public class DownloadManager {

    private DownloadReferenceStore downloadReferenceStore;

    private FileManager fileManager;

    private RecordRepository recordRepository;

    private TransactionTemplate transactionTemplate;
    @Value("${eil.download.image.batch-size}")
    private int imageBatchSize;

    public void queueImageDownloads(Set<Link> links) {
        links.forEach(link -> {
            DownloadReference downloadReference = new DownloadReference();
            downloadReference.setUrl(link.getUrl());
            downloadReference.setRecord(link.getRecord());
            downloadReference.setReferencedAttribute(selectAttribute(link.getDescription()));
            downloadReferenceStore.create(downloadReference);
            }
        );
    }

    @Scheduled(fixedDelay = 1000 * 30)
    public void runDownloads() {
        long count = downloadReferenceStore.countRemainingDownloads(imageBatchSize);
        for (int i = 0; i < count; i++){
            DownloadReference downloadReference = downloadReferenceStore.getNext();
            downloadAndSetToEntity(downloadReference);
        }
    }

    @Scheduled(cron = "${eil.cron.download.failed}")
    public void runFailedDownloads() {
        List<DownloadReference> failed = downloadReferenceStore.fetchFailedDownloads();
        failed.forEach(this::downloadAndSetToEntity);
    }

    private void downloadAndSetToEntity(DownloadReference downloadReference) {
        File file = downloadFile(downloadReference);
        try {
            if (file != null) {
                Record record = downloadReference.getRecord();
                switch (downloadReference.getReferencedAttribute()) {
                    case ILLUSTRATION:
                        ((Illustration) record).setIllustrationScan(file);
                        break;
                    case ILLUSTRATION_PAGE:
                        ((Illustration) record).setPageScan(file);
                        break;
                    case FRONT_PAGE:
                        ((Book) record).setFrontPageScan(file);
                        break;
                }
                transactionTemplate.executeWithoutResult(status -> recordRepository.update(record));
                downloadReference.setDownloaded(true);
                transactionTemplate.executeWithoutResult(status -> downloadReferenceStore.update(downloadReference));
                log.info("Image " + file.getName() + " set to record " + record.getIdentifier() + " successfully.");
            }
        } catch (ClassCastException ex) {
            transactionTemplate.executeWithoutResult(status -> fileManager.remove(file.id));
            removeFileReference(downloadReference, file.getId());
            downloadReference.setFailed(true);
            transactionTemplate.executeWithoutResult(status -> downloadReferenceStore.update(downloadReference));
            Record record = downloadReference.getRecord();
            log.error("Image download error: Problem with class casting record " + record.getType().toLowerCase() + " " + record.id);
        }
    }

    private static ImageForDownload selectAttribute(String description) {
        description = description.trim().toLowerCase();
        for (ImageForDownload forDownload : ImageForDownload.values()) {
            if (description.startsWith(forDownload.getLabel())) {
                return forDownload;
            }
        }
        throw new InvalidArgument(UNRECOGNIZED_LINK_DESCRIPTION,
                "Link description '" + description + "' not recognized " +
                "as Record entity attribute when preparing file to download from url.");
    }

    private File downloadFile(DownloadReference downloadReference) {
        try {
            byte[] data = new URL(downloadReference.getUrl()).openStream().readAllBytes();
            InputStream is = new ByteArrayInputStream(data);
            if (downloadReference.record == null) {
                transactionTemplate.executeWithoutResult(status -> downloadReferenceStore.delete(downloadReference.getId()));
                log.debug("Deleted download reference {}", downloadReference.url);
                return null;
            }
            String filename = createFileName(downloadReference);
            return fileManager.store(filename, data.length, MediaType.IMAGE_JPEG_VALUE, is);
        } catch (IOException ex) {
            if (downloadReference.getUrl() == null || downloadReference.getRecord() == null || downloadReference.getRecord().getIdentifier() == null) {
                log.debug("DownloadReference '{}' is corrupted.", downloadReference.getId());
                transactionTemplate.executeWithoutResult(status -> downloadReferenceStore.delete(downloadReference.getId()));
                log.debug("Deleted download reference {}", downloadReference.getId());
            } else {
                log.warn("Failed to download image for record " + downloadReference.getRecord().getIdentifier() + " from url" + downloadReference.getUrl());
                downloadReference.setFailed(true);
                transactionTemplate.executeWithoutResult(status -> downloadReferenceStore.update(downloadReference));
            }
        }
        return null;
    }

    private String createFileName(DownloadReference downloadReference) {
        return String.join("_", "REC",
                downloadReference.getRecord().getIdentifier(),
                downloadReference.getReferencedAttribute().name().toLowerCase()) + ".jpg";
    }

    public void removeFileReference(DownloadReference downloadReference, String fileId) {
        Record record = downloadReference.getRecord();
        File file;

        if (record != null) {
            if (record instanceof Illustration) {
                file = ((Illustration) record).getIllustrationScan();
                if (file != null && file.getId().equals(fileId)) {
                    ((Illustration) record).setIllustrationScan(null);
                    transactionTemplate.executeWithoutResult(status -> recordRepository.update(record));
                }

                file = ((Illustration) record).getPageScan();
                if (file != null && file.getId().equals(fileId)) {
                    ((Illustration) record).setPageScan(null);
                    transactionTemplate.executeWithoutResult(status -> recordRepository.update(record));
                }
            } else {
                file = ((Book) record).getFrontPageScan();
                if (file != null && file.getId().equals(fileId)) {
                    ((Book) record).setFrontPageScan(null);
                    transactionTemplate.executeWithoutResult(status -> recordRepository.update(record));
                }
            }
        }
    }

    @Autowired
    public void setDownloadReferenceStore(DownloadReferenceStore downloadReferenceStore) {
        this.downloadReferenceStore = downloadReferenceStore;
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
