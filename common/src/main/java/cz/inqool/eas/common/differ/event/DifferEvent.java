package cz.inqool.eas.common.differ.event;

import cz.inqool.eas.common.differ.DifferEventNotifier;
import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.util.Map;

/**
 * @param <T> type for which to resolve spring's EventListeners
 * @see DifferEventNotifier notifier for publishing differ events
 */
@Getter
public abstract class DifferEvent<T extends Domain<?>> extends ApplicationEvent implements ResolvableTypeProvider {

    /**
     * Flag allowing conditionally processing in case of chaining events
     */
    private boolean processed = false;
    /**
     * ROOT type of entity, even if changes are computed for a Views,
     * because listener of differ events will be declared for root entity and not one listener per view
     */
    private final Class<T> rootType;

    private final DifferActionType action;

    public DifferEvent(Object source, Class<T> rootType, DifferActionType action) {
        super(source);
        this.rootType = rootType;
        this.action = action;
    }

    public boolean isAfterMerge() {
        return this.action == DifferActionType.AFTER_CREATE || this.action == DifferActionType.AFTER_UPDATE;
    }

    public boolean isBeforeMerge() {
        return this.action == DifferActionType.BEFORE_CREATE || this.action == DifferActionType.BEFORE_UPDATE;
    }

    public boolean isFromCreate() {
        return this.action == DifferActionType.BEFORE_CREATE || this.action == DifferActionType.AFTER_CREATE;
    }

    public boolean isFromUpdate() {
        return this.action == DifferActionType.BEFORE_UPDATE || this.action == DifferActionType.AFTER_UPDATE;
    }

    @Override
    public ResolvableType getResolvableType() {
        // this type must be equal to the event type that is awaited with @EventListener e.g. DifferEvent<Document>
        return ResolvableType.forClassWithGenerics(getClass(), rootType);
    }

    /**
     * < RealType: < Ancestry path : Change >>
     *
     * Using Map of Maps because payload can be for COLLECTION create/update!
     * Where there can be multiple classes as keys and for each class different change set there was a diff computed.
     */
    public abstract Map<Domain<?>, Map<String, DifferChange>> getPayload();

    public abstract Map<Domain<?>, Map<String, DifferChange>> getChangedPayload();

    public void declareProcessed() {
        this.processed = true;
    }

}
