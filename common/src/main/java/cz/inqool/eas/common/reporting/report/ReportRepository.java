package cz.inqool.eas.common.reporting.report;

import cz.inqool.eas.common.authored.AuthoredRepository;
import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import static cz.inqool.eas.common.module.Modules.REPORTING;

public class ReportRepository extends AuthoredRepository<
        Report,
        Report,
        ReportIndexedObject,
        AuthoredStore<Report, Report, QReport>,
        AuthoredIndex<Report, Report, ReportIndexedObject>> {

    public ReportDetail getLastByDefinition(String definitionId, String userId) {
        QReportDetail model = QReportDetail.reportDetail;

        ReportDetail reportSetting = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.definitionId.eq(definitionId))
                .where(model.createdBy.id.eq(userId))
                .orderBy(model.created.desc())
                .fetchFirst();

        detachAll();

        return reportSetting;
    }

    @Override
    protected ModuleDefinition getModule() {
        return REPORTING;
    }
}
