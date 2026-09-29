package cz.inqool.eas.common.domain.event.store;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.store.DomainStore;
import lombok.Getter;

public class StorePreUpdateEvent<ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> extends StoreMergeEvent<ROOT, PROJECTED> {

    @Getter
    private final PROJECTED oldEntity;
    public StorePreUpdateEvent(DomainStore<ROOT, PROJECTED, ?> store, PROJECTED oldEntity, PROJECTED newEntity) {
        super(store, newEntity);
        this.oldEntity = oldEntity;
    }
}
