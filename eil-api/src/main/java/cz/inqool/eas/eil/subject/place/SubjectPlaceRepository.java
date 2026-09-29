package cz.inqool.eas.eil.subject.place;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectPlaceRepository extends DatedRepository<
        SubjectPlace,
        SubjectPlaceIndexed,
        SubjectPlaceIndexedObject,
        DatedStore<SubjectPlace, SubjectPlace, QSubjectPlace>,
        DatedIndex<SubjectPlace, SubjectPlaceIndexed, SubjectPlaceIndexedObject>> {

    public SubjectPlace findByName(String name) {
        if (name == null) {
            return null;
        }
        QSubjectPlace model = QSubjectPlace.subjectPlace;

        SubjectPlace place = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.name.eq(name))
                .fetchOne();

        detachAll();
        return place;
    }

    @Override
    public int getReindexBatchSize() {
        return 1000;
    }
}
