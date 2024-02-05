package cz.inqool.eas.common.differ.model;

import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import org.springframework.lang.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * ConcurrentSkipListMap - concurrent version of TreeMap
 *   - beware of bulk operations such as putAll (see JavaDoc)
 */
public class DifferPropertiesMap extends ConcurrentSkipListMap<String, DifferProperty<?>> {

    /**
     * Puts field and its subfields into map.
     *
     * Map key: {@link DifferProperty#getAncestryPath}
     * Map value: {@link DifferProperty}
     *
     * @param differProperty to be put into map.
     */
    public void putField(@NonNull DifferProperty<?> differProperty, boolean overwrite) {
        String ancestryPath = differProperty.getAncestryPath();

        if (overwrite) {
            super.put(ancestryPath, differProperty);
        } else {
            super.putIfAbsent(ancestryPath, differProperty);
        }

        for (DifferProperty<?> subField : differProperty.getSubProperties()) {
            putField(subField, overwrite);
        }
    }

    public void mergeWith(@NonNull DifferPropertiesMap map) {
        map.values().forEach(differProperty -> putField(differProperty, true));
    }

    public void retainOnly(String[] fieldsToRetain) {
        if (fieldsToRetain == null || fieldsToRetain.length == 0) {
            return;
        }

        Set<String> toRetainKeys = new HashSet<>(List.of(fieldsToRetain));

        Set<String> toRemoveKeys = new HashSet<>();
        this.keySet().forEach(key -> {
            if (!toRetainKeys.contains(key)) {
                // first collect for-removal keys to avoid removing keys while iterating over them
                toRemoveKeys.add(key);
            }
        });

        toRemoveKeys.forEach(this::remove);
    }


    public void removeAll(String[] fieldsToRemove) {
        if (fieldsToRemove == null || fieldsToRemove.length == 0) {
            return;
        }

        Set<String> toRemoveKeys = new HashSet<>();

        for (String toRemoveKey : fieldsToRemove) {
            // all keys that start with key that should be removed
            toRemoveKeys.addAll(this.keySet().stream().filter(key -> key.startsWith(toRemoveKey)).collect(Collectors.toSet()));
        }

        toRemoveKeys.forEach(this::remove);
    }

    @Override
    public String toString() {
        return "[" + String.join(", ", this.keySet()) + "]";
    }

}
