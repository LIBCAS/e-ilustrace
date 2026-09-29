package cz.inqool.eas.common.export.batch;

import cz.inqool.eas.common.authored.AuthoredRepository;
import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.common.export.request.ExportRequest;
import cz.inqool.eas.common.export.request.ExportRequestState;
import cz.inqool.eas.common.module.ModuleDefinition;

import static cz.inqool.eas.common.module.Modules.EXPORT;

public class ExportBatchRepository extends AuthoredRepository<
        ExportBatch,
        ExportBatch,
        ExportBatchIndexedObject,
        AuthoredStore<ExportBatch, ExportBatch, QExportBatch>,
        AuthoredIndex<ExportBatch, ExportBatch, ExportBatchIndexedObject>
        > {

    public ExportBatch findByRequest(ExportRequest request) {
        QExportBatch model = QExportBatch.exportBatch;

        ExportBatch batch = query().
                select(model).
                from(model).
                where(model.deleted.isNull()).
                where(model.requests.contains(request))
                .fetchFirst();

        detachAll();

        return batch;
    }

    @Override
    protected ModuleDefinition getModule() {
        return EXPORT;
    }
}
