package cz.inqool.eas.common.differ.strategy.impl;

import cz.inqool.eas.common.differ.model.DiffedType;
import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import cz.inqool.eas.common.differ.strategy.ComparingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

// beaned in DifferConfiguration
@Slf4j
public class EmbeddedComparingStrategy implements ComparingStrategy {

    @Override
    public DiffedType getType() {
        return DiffedType.EMBEDDED;
    }

    @Override
    public Set<DifferChange> diffObjects(@Nullable Object oldObject, @NonNull Object newObject, DifferProperty<?> differField) {
        Object oldInstance = extractFieldValue(oldObject, differField);
        Object newInstance = extractFieldValue(newObject, differField);

        Set<DifferChange> changes = new LinkedHashSet<>();

        boolean valuesAreEqual;
        if (differField.getSubProperties().isEmpty()) {
            // naive comparison... embedded class should always have subFields
            valuesAreEqual = Objects.equals(oldInstance, newInstance);

            changes.add(new DifferChange(differField, oldInstance, newInstance, valuesAreEqual));
        } else {
            boolean currentLevelEqual = true;

            for (DifferProperty<?> subField : differField.getSubProperties()) {
                Set<DifferChange> subFieldChangeSet = subField.diffObjects(oldInstance, newInstance);

                changes.addAll(subFieldChangeSet);

                // true if all subfields (recursively) have equal values, false if at least one non-equality detected (on any level)
                boolean allEqualOnCurrentLevel = subFieldChangeSet.stream()
                        .map(DifferChange::valuesAreEqual)
                        .reduce(true, Boolean::logicalAnd);

                if (!allEqualOnCurrentLevel) currentLevelEqual = false;
            }

            changes.add(
                    new DifferChange(differField, oldInstance, newInstance, currentLevelEqual)
            );
        }

        return changes;
    }

}
