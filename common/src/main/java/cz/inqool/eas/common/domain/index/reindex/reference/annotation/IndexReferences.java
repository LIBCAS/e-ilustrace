package cz.inqool.eas.common.domain.index.reindex.reference.annotation;

import java.lang.annotation.*;

/**
 * A wrapper to allow a list of multiple {@link IndexReference} objects.
 *
 * @see IndexReference
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexReferences {

    /**
     * A list of {@link IndexReference}s to a field.
     */
    IndexReference[] value() default {};
}
