package cz.inqool.eas.common.differ.strategy.impl;

import cz.inqool.eas.common.differ.model.DiffedType;
import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import cz.inqool.eas.common.differ.strategy.ComparingStrategy;
import cz.inqool.eas.common.domain.Domain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Set;

// beaned in DifferConfiguration
@Slf4j
public class EntityComparingStrategy implements ComparingStrategy {

    @Override
    public DiffedType getType() {
        return DiffedType.ENTITY;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Set<DifferChange> diffObjects(@Nullable Object oldObject, @NonNull Object newObject, DifferProperty<?> differField) {
        Object oldValue = extractFieldValue(oldObject, differField);
        Object newValue = extractFieldValue(newObject, differField);

        boolean valuesAreEqual;

        if (oldValue == null && newValue == null) {
            valuesAreEqual = true;
        } else if (oldValue == null && newValue != null) {
            valuesAreEqual = false;
        } else if (oldValue != null && newValue == null) {
            valuesAreEqual = false;
        }

        // continue where both values are not null

        // Entities must implement Domain interface to compare them by ID
        else if (oldValue instanceof Domain<?> && newValue instanceof Domain<?>) {
            Domain<?> oldCasted = (Domain<?>) oldValue;
            Domain<?> newCasted = (Domain<?>) newValue;
            valuesAreEqual = Objects.equals(oldCasted.getId(), newCasted.getId());
        } else {
            log.warn("Attempting to use comparing strategy '{}' to compare values: {}, {}. "
                     + "But these values are not instances of 'cz.inqool.eas.common.domain.Domain'. "
                     + "Defaulting to returning FALSE for equality comparison.",
                    getClass().getSimpleName(), oldValue, newValue);
            valuesAreEqual = false;
        }

        return Set.of(
                new DifferChange(differField, oldValue, newValue, valuesAreEqual)
        );
    }

}
