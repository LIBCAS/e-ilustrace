package cz.inqool.eas.common.differ.event;

import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.domain.Domain;

import java.util.Map;
import java.util.stream.Collectors;

public class DifferCollectionActionEvent<T extends Domain<?>> extends DifferEvent<T> {


    private final Map<Domain<?>, Map<String, DifferChange>> changesMap;

    public DifferCollectionActionEvent(Object source, Class<T> rootType, DifferActionType action, Map<Domain<?>, Map<String, DifferChange>> changesMap) {
        super(source, rootType, action);
        this.changesMap = changesMap;
    }

    @Override
    public Map<Domain<?>, Map<String, DifferChange>> getPayload() {
        return changesMap;
    }

    @Override
    public Map<Domain<?>, Map<String, DifferChange>> getChangedPayload() {
        return changesMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                                .entrySet()
                                .stream()
                                .filter(e -> !e.getValue().valuesAreEqual())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                ));
    }
}
