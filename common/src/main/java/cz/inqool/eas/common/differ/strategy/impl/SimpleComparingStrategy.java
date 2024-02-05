package cz.inqool.eas.common.differ.strategy.impl;

import cz.inqool.eas.common.differ.model.DiffedType;
import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import cz.inqool.eas.common.differ.strategy.ComparingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Set;

// beaned in DifferConfiguration
@Slf4j
public class SimpleComparingStrategy implements ComparingStrategy {

    @Override
    public DiffedType getType() {
        return DiffedType.SIMPLE;
    }

    @Override
    public Set<DifferChange> diffObjects(@Nullable Object oldObject, @NonNull Object newObject, DifferProperty<?> differField) {
        Object oldValue = extractFieldValue(oldObject, differField);
        Object newValue = extractFieldValue(newObject, differField);

        boolean valuesAreEqual = Objects.equals(oldValue, newValue);

        return Set.of(
                new DifferChange(differField, oldValue, newValue, valuesAreEqual)
        );
    }

}
