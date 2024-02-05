package cz.inqool.eas.common.differ.model.prop;

import cz.inqool.eas.common.differ.exception.DifferException;
import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.differ.strategy.ComparingStrategy;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.AccessibleObject;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public abstract class DifferProperty<PROPERTY extends AccessibleObject> implements Comparable<DifferProperty<PROPERTY>> {
    protected final ComparingStrategy comparingStrategy;

    /**
     * Name of the attribute
     */
    protected final String propertyName;

    protected final PROPERTY javaProperty;

    /**
     * Example of ancestry path for 'currentField': 'first.second.third.currentField'
     */
    protected final String ancestryPath;

    /**
     * Class of attribute / method return class
     *
     *
     * If field is collection, then type = COLLECTION, and this attribute will be of the generic type inside collection
     */
    protected final Class<?> propertyClazz;

    /**
     * Origin class where attribute is declared, where it resides,
     * can be entity or embeddable class  (TODO possibly something else also? )
     */
    protected final Class<?> residencyClazz;

    /**
     * If this field has a parent then it is an inner attribute, parent will contain this field in its subFields
     *
     * Parent of attribute/method can be both attribute or method.
     *
     * If return type of method is nested object (e.g. embedded), that object can be possibly parsed by differ.
     * (parent of attribute is then method)
     *
     * If an attribute is nested object, it can have method properties. (parent of method is then attribute)
     */
    @Nullable
    protected final DifferProperty<?> parentProperty;

    /**
     * Subfield, e.g. @Embedded attribute is a class with additional attributes or methods
     */
    protected final Set<DifferProperty<?>> subProperties = new LinkedHashSet<>();

    public DifferProperty(String propertyName, PROPERTY javaProperty, ComparingStrategy strategy, Class<?> residencyClazz, @Nullable DifferProperty<?> parentProperty) {
        this.propertyName = propertyName;
        this.comparingStrategy = strategy;
        this.residencyClazz = residencyClazz;
        this.parentProperty = parentProperty;
        this.javaProperty = javaProperty;
        this.propertyClazz = propertyClass(javaProperty);
        this.ancestryPath = ancestryPath();
    }

    /**
     * Diff objects on current java property (attribute/method).
     * Invoke/obtain value of java property from objects and compare them with comparing strategy.
     */
    public <OBJ> Set<DifferChange> diffObjects(@Nullable OBJ oldObject, @NonNull OBJ newObject) {
        return comparingStrategy.diffObjects(oldObject, newObject, this);
    }

    @Nullable
    public abstract Object getValue(@Nullable Object entity) throws DifferException;

    /**
     * Construct ancestry path: 'first.second.third.currentField'
     *
     * @return ancestry path consisting of names of parents + name of this field
     */
    private String ancestryPath() {
        StringBuilder pathBuilder = new StringBuilder();

        DifferProperty<?> currentField = this;
        while (currentField != null) {
            if (pathBuilder.length() != 0) {
                pathBuilder.insert(0, ".");
            }
            pathBuilder.insert(0, currentField.getPropertyName());
            currentField = currentField.getParentProperty();
        }

        return pathBuilder.toString();
    }

    public abstract Class<?> propertyClass(PROPERTY o);

    @Override
    public int compareTo(@NotNull DifferProperty other) {
        return this.ancestryPath.compareTo(other.ancestryPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DifferProperty<?> that = (DifferProperty<?>) o;
        return javaProperty.equals(that.javaProperty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaProperty);
    }

    @Override
    public String toString() {
        // DifferMethod: (String) Entity.property
        return String.format("%s{(%s) %s.%s}", getClass().getSimpleName(), propertyClazz.getSimpleName(), residencyClazz.getSimpleName(), getPropertyName());
    }

    public boolean isSubProperty() {
        return parentProperty != null;
    }

    public boolean isNotSubProperty() {
        return !isSubProperty();
    }
}
