package cz.inqool.eas.common.differ.model;

import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public class DifferChange {

    private final Class<?> fieldClass;
    private final DiffedType fieldType;
    private final String fieldName;
    private final String ancestryPath;
    private final Object oldValue;
    private final Object newValue;
    @Accessors(fluent = true) // 'is' prefix will not be generated for the getter
    private final boolean valuesAreEqual;


    public DifferChange(DifferProperty<?> field, Object oldValue, Object newValue, boolean valuesAreEqual) {
        this.fieldClass = field.getPropertyClazz();
        this.fieldType = field.getComparingStrategy().getType();
        this.fieldName = field.getPropertyName();
        this.ancestryPath = field.getAncestryPath();
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.valuesAreEqual = valuesAreEqual;
    }

}