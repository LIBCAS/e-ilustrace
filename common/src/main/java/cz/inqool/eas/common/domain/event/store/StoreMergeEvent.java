package cz.inqool.eas.common.domain.event.store;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.store.DomainStore;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.lang.NonNull;

@Getter
public abstract class StoreMergeEvent<ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> extends ApplicationEvent implements ResolvableTypeProvider {

    private final DomainStore<ROOT, PROJECTED, ?> store;
    private final PROJECTED newEntity;

    public StoreMergeEvent(DomainStore<ROOT, PROJECTED, ?> store, @NonNull PROJECTED newEntity) {
        super(store);
        this.store = store;
        this.newEntity = newEntity;
    }

    /**
     * Event listener example:
     *
     * {@code public void listen(StoreMergeEventImpl<PROJECTED>}
     *
     * Where StoreMergeEventImpl is som subclass of this abstract class.
     * And PROJECTED type is type of store (can be an entity view)
     */
    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), store.getRootType(), store.getType());
    }

}
