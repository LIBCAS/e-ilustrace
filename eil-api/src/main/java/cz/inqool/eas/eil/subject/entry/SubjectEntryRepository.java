package cz.inqool.eas.eil.subject.entry;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectEntryRepository extends DatedRepository<
        SubjectEntry,
        SubjectEntryIndexed,
        SubjectEntryIndexedObject,
        DatedStore<SubjectEntry, SubjectEntry, QSubjectEntry>,
        DatedIndex<SubjectEntry, SubjectEntryIndexed, SubjectEntryIndexedObject>> {

    public SubjectEntry findByLabel(String label) {
        if (label == null) {
            return null;
        }
        QSubjectEntry model = QSubjectEntry.subjectEntry;

        SubjectEntry subjectEntry = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.label.eq(label))
                .fetchOne();

        detachAll();
        return subjectEntry;
    }

    @Override
    public int getReindexBatchSize() {
        return 1000;
    }
}
