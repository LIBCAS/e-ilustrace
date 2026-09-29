package cz.inqool.eas.eil.subject.person;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubjectPersonRepository extends DatedRepository<
        SubjectPerson,
        SubjectPersonIndexed,
        SubjectPersonIndexedObject,
        DatedStore<SubjectPerson, SubjectPerson, QSubjectPerson>,
        DatedIndex<SubjectPerson, SubjectPersonIndexed, SubjectPersonIndexedObject>> {

    public List<SubjectPerson> findByFullName(String fullName) {
        QSubjectPerson model = QSubjectPerson.subjectPerson;

        List<SubjectPerson> subjectPerson = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.fullName.eq(fullName))
                .fetch();

        detachAll();
        return subjectPerson;
    }

    public SubjectPerson findByFullNameAndYears(String fullName, String birthYear, String deathYear) {
        QSubjectPerson model = QSubjectPerson.subjectPerson;

        SubjectPerson subjectPerson = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.fullName.eq(fullName))
                .where(model.birthYear.eq(birthYear))
                .where(model.deathYear.eq(deathYear))
                .fetchOne();

        detachAll();
        return subjectPerson;
    }

    @Override
    public int getReindexBatchSize() {
        return 1000;
    }
}
