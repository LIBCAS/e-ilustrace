package cz.inqool.eas.common.schedule.run;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import java.util.List;

import static cz.inqool.eas.common.module.Modules.SCHEDULING;

public class RunRepository extends DatedRepository<
        Run,
        Run,
        RunIndexedObject,
        DatedStore<Run, Run, QRun>,
        DatedIndex<Run, Run, RunIndexedObject>> {

    @Override
    protected ModuleDefinition getModule() {
        return SCHEDULING;
    }

    public Run getLast(String jobId) {

        QRun model = getStore().getMetaModel();

        Run run = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.job.id.eq(jobId))
                .orderBy(model.startTime.desc())
                .fetchFirst();

        detachAll();

        return run;
    }
}
