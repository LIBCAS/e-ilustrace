package cz.inqool.eas.eil.institution;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class InstitutionRepository extends DatedRepository<
        Institution,
        Institution,
        InstitutionIndexedObject,
        DatedStore<Institution, Institution, QInstitution>,
        DatedIndex<Institution, Institution, InstitutionIndexedObject>> {

    public Institution findByName(String name) {
        if (name == null) {
            return null;
        }
        QInstitution model = QInstitution.institution;

        Institution institution = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.name.eq(name))
                .fetchOne();

        detachAll();
        return institution;
    }
}
