package cz.inqool.eas.common.domain.event.store;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.store.DomainStore;

import java.util.Collection;

public class StorePreCreateCollectionEvent<ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> extends StoreMergeCollectionEvent<ROOT, PROJECTED> {

    public StorePreCreateCollectionEvent(DomainStore<ROOT, PROJECTED, ?> store, Collection<? extends PROJECTED> newEntities) {
        super(store, newEntities);
    }
}
