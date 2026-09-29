package cz.inqool.eas.common.domain.index.field;

import java.lang.annotation.*;

/**
 * Allows boost score on indexed field for fulltext search.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Boost {
    /**
     * Boost factor
     */
    float value() default 1;
}
