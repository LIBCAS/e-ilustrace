package cz.inqool.eas.common.export.request;

import cz.inqool.eas.common.authored.AuthoredRepository;
import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import java.time.Instant;
import java.util.List;

import static cz.inqool.eas.common.module.Modules.EXPORT;

public class ExportRequestRepository extends AuthoredRepository<
        ExportRequest,
        ExportRequest,
        ExportRequestIndexedObject,
        AuthoredStore<ExportRequest, ExportRequest, QExportRequest>,
        AuthoredIndex<ExportRequest, ExportRequest, ExportRequestIndexedObject>
        > {

    /**
     * Returns export request with the highest priority in state {@link ExportRequestState#PENDING}. Requests with no
     * defined priority are returned at last. Only export requests created before {@code until} will be considered.
     */
    public String findNextToProcess(Instant until) {
        QExportRequest model = QExportRequest.exportRequest;

        return query().
                select(model.id).
                from(model).
                where(model.deleted.isNull()).
                where(model.created.before(until)).
                where(model.state.eq(ExportRequestState.PENDING)).
                where(model.systemRequest.isFalse()).
                orderBy(model.priority.desc().nullsLast())
                .fetchFirst();
    }

    /**
     * Returns export request with the highest priority in state {@link ExportRequestState#PENDING}. Requests with no
     * defined priority are returned at last. Only export requests created before {@code until} will be considered.
     */
    public ExportRequest findNextToProcess() {
        QExportRequest model = QExportRequest.exportRequest;

        return query().
                select(model).
                from(model).
                where(model.deleted.isNull()).
                where(model.state.eq(ExportRequestState.PENDING)).
                where(model.systemRequest.isFalse()).
                orderBy(model.priority.coalesce(0).desc())
                .fetchFirst();
    }

    /**
     * Returns export request in state {@link ExportRequestState#PENDING} which were not processed in time limit.
     */
    public List<ExportRequest> listForgotten(int timeLimit) {
        QExportRequest model = QExportRequest.exportRequest;

        Instant now = Instant.now();
        Instant limit = now.minusSeconds(timeLimit);

        return query().
                select(model).
                from(model).
                where(model.deleted.isNull()).
                where(model.state.eq(ExportRequestState.PROCESSING)).
                where(model.systemRequest.isFalse()).
                where(model.processingStart.before(limit)).
                fetch();
    }

    @Override
    protected ModuleDefinition getModule() {
        return EXPORT;
    }
}
