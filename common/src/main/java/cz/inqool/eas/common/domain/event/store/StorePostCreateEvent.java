package cz.inqool.eas.common.domain.event.store;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.store.DomainStore;

public class StorePostCreateEvent<ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> extends StoreMergeEvent<ROOT, PROJECTED> {

    public StorePostCreateEvent(DomainStore<ROOT, PROJECTED, ?> store, PROJECTED newEntity) {
        super(store, newEntity);
    }
}
