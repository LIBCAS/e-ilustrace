package cz.inqool.entityviews;


import java.lang.annotation.*;

/**
 * A wrapper to allow a list of multiple {@link ViewableAnnotation} objects.
 *
 * @see ViewableMapping
 */
@Inherited
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewableAnnotations {

    /**
     * A list of {@link ViewableAnnotation}s to a field.
     */
    ViewableAnnotation[] value();
}
