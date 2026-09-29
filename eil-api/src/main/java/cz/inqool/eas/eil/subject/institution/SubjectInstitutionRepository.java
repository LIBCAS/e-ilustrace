package cz.inqool.eas.eil.subject.institution;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectInstitutionRepository extends DatedRepository<
        SubjectInstitution,
        SubjectInstitution,
        SubjectInstitutionIndexedObject,
        DatedStore<SubjectInstitution, SubjectInstitution, QSubjectInstitution>,
        DatedIndex<SubjectInstitution, SubjectInstitution, SubjectInstitutionIndexedObject>> {

    public SubjectInstitution findByName(String name) {
        if (name == null) {
            return null;
        }
        QSubjectInstitution model = QSubjectInstitution.subjectInstitution;

        SubjectInstitution institution = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.name.eq(name))
                .fetchOne();

        detachAll();
        return institution;
    }
}
