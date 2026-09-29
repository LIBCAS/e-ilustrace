package cz.inqool.eas.common.domain.event.store;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.store.DomainStore;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

public class StorePostUpdateCollectionEvent<ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> extends StoreMergeCollectionEvent<ROOT, PROJECTED> {

    @Getter
    private final List<PROJECTED> oldEntities;

    public StorePostUpdateCollectionEvent(DomainStore<ROOT, PROJECTED, ?> store, Collection<? extends PROJECTED> oldEntities, Collection<? extends PROJECTED> newEntities) {
        super(store, newEntities);
        this.oldEntities = List.copyOf(oldEntities);
    }
}
