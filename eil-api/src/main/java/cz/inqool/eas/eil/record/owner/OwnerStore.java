package cz.inqool.eas.eil.record.owner;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class OwnerStore extends DomainStore<Owner, Owner, QOwner> {

    public OwnerStore() {
        super(Owner.class);
    }
}
