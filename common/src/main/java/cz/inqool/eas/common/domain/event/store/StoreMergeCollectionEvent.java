package cz.inqool.eas.common.domain.event.store;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.store.DomainStore;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

@Getter
public abstract class StoreMergeCollectionEvent<ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> extends ApplicationEvent implements ResolvableTypeProvider {

    private final DomainStore<ROOT, PROJECTED, ?> store;
    private final List<PROJECTED> newEntities;

    public StoreMergeCollectionEvent(DomainStore<ROOT, PROJECTED, ?> store, @NonNull Collection<? extends PROJECTED> newEntities) {
        super(store);
        this.store = store;
        this.newEntities = List.copyOf(newEntities);
    }

    @Override
    public ResolvableType getResolvableType() {
        // fixme
        return ResolvableType.forClassWithGenerics(getClass(), store.getRootType(), store.getType());
    }
}
