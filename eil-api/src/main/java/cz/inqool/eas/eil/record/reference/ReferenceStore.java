package cz.inqool.eas.eil.record.reference;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class ReferenceStore extends DomainStore<Reference, Reference, QReference> {

    public ReferenceStore() {
        super(Reference.class);
    }
}
