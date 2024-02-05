package cz.inqool.eas.common.schedule.job;

import cz.inqool.eas.common.dictionary.DictionaryRepository;
import cz.inqool.eas.common.dictionary.index.DictionaryIndex;
import cz.inqool.eas.common.dictionary.store.DictionaryStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import static cz.inqool.eas.common.module.Modules.SCHEDULING;

public class JobRepository extends DictionaryRepository<
        Job,
        Job,
        JobIndexedObject,
        DictionaryStore<Job, Job, QJob>,
        DictionaryIndex<Job, Job, JobIndexedObject>> {

    @Override
    protected ModuleDefinition getModule() {
        return SCHEDULING;
    }
}
