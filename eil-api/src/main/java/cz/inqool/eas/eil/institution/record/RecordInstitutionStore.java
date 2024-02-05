package cz.inqool.eas.eil.institution.record;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class RecordInstitutionStore extends DomainStore<RecordInstitution, RecordInstitution, QRecordInstitution> {

    public RecordInstitutionStore() {
        super(RecordInstitution.class);
    }
}
