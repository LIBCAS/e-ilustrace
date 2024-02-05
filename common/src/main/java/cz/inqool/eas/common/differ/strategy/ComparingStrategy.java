package cz.inqool.eas.common.differ.strategy;

import cz.inqool.eas.common.differ.model.DiffedType;
import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Set;

public interface ComparingStrategy {

    DiffedType getType();

    @Nullable
    default Object extractFieldValue(@Nullable Object object, DifferProperty<?> differField) {
        return differField.getValue(object);
    }

    /**
     * Compares differentiable field of two objects by creating set of changes.
     *
     * It is a set because comparison of a field can be multi-level (e.g. for embedded class instance)
     *
     * @return set of detected changes for given differentiable field
     */
    Set<DifferChange> diffObjects(@Nullable Object oldObject, @NonNull Object newObject, DifferProperty<?> differField);

}
