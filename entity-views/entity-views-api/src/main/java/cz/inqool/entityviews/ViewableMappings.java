package cz.inqool.entityviews;


import java.lang.annotation.*;

/**
 * A wrapper to allow a list of multiple {@link ViewableMapping} objects.
 *
 * @see ViewableMapping
 */
@Inherited
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewableMappings {

    /**
     * A list of {@link ViewableMapping}s to a field.
     */
    ViewableMapping[] value();
}
