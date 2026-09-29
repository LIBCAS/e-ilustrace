package cz.inqool.eas.common.differ.event;

import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class DifferSingleActionEvent<T extends Domain<?>> extends DifferEvent<T> {

    /**
     * This entity can be a view!
     * DifferSingleActionEvent<MyEntity> does not guarantee that this entity field will be of type MyEntity,
     * it can be MyEntityView!
     *
     * @see #getResolvableType
     */
    private final Domain<?> entity;
    private final Map<String, DifferChange> changes;

    public DifferSingleActionEvent(Object source, Class<T> rootType, DifferActionType action, Domain<?> entity, Map<String, DifferChange> changes) {
        super(source, rootType, action);
        this.entity = entity;
        this.changes = changes;
    }

    public Map<String, DifferChange> changes() {
        return this.changes;
    }

    public Domain<?> entity() {
        return this.entity;
    }

    @Override
    public Map<Domain<?>, Map<String, DifferChange>> getPayload() {
        return Map.of(entity, changes);
    }

    @Override
    public Map<Domain<?>, Map<String, DifferChange>> getChangedPayload() {
        return Map.of(
                entity,
                changes.entrySet()
                        .stream()
                        .filter(e -> !e.getValue().valuesAreEqual())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
}
