package cz.inqool.eas.common.differ.strategy.impl;

import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import cz.inqool.eas.common.differ.strategy.ComparingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Slf4j
public abstract class CollectionComparingStrategy implements ComparingStrategy {

    /**
     * @see Collection#equals javadoc
     */
    @Override
    public Set<DifferChange> diffObjects(@Nullable Object oldObject, @NonNull Object newObject, DifferProperty<?> differField) {
        Object oldValue = extractFieldValue(oldObject, differField);
        Object newValue = extractFieldValue(newObject, differField);

        boolean valuesAreEqual = false;

        // both null
        if (oldValue == null && newValue == null) {
            return Set.of(new DifferChange(differField, oldValue, newValue, true));
        }
        // one of the values is null
        if (oldValue == null || newValue == null) {
            return Set.of(new DifferChange(differField, oldValue, newValue, false));
        }

        // continue when both not null

        if (oldValue instanceof Collection<?> && newValue instanceof Collection<?>) {
            Collection<?> oldCasted = (Collection<?>) oldValue;
            Collection<?> newCasted = (Collection<?>) newValue;

            // each collection implementation has its own contact for equals,
            // but generally it will compare length, order and collection elements themselves
            valuesAreEqual = Objects.equals(oldCasted, newCasted);
        } else {
            log.warn("Attempting to use comparing strategy '{}' to compare values: {}, {} for differ field {} with path {}. But these values are not instances of 'java.util.Collection'. Defaulting to returning FALSE for equality comparison.", getClass().getSimpleName(), differField, oldValue, newValue, differField.getAncestryPath());
        }

        return Set.of(
                new DifferChange(differField, oldValue, newValue, valuesAreEqual)
        );
    }

}
