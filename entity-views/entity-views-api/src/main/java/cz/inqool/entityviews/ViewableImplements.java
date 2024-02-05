package cz.inqool.entityviews;


import java.lang.annotation.*;

/**
 * A wrapper to allow a list of multiple {@link ViewableImplement} objects.
 *
 * @see ViewableMapping
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewableImplements {

    /**
     * A list of {@link ViewableImplement}s to a field.
     */
    ViewableImplement[] value();
}
