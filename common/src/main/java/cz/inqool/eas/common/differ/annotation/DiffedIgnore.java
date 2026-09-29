package cz.inqool.eas.common.differ.annotation;

import java.lang.annotation.*;

/**
 * Declares that ATTRIBUTE or METHOD of the entity shall be ignored in diffing.
 *
 * @see DiffedClass JavaDoc
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface DiffedIgnore {
}
