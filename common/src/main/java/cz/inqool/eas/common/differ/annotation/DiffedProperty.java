package cz.inqool.eas.common.differ.annotation;

import cz.inqool.eas.common.differ.parser.DefaultDifferPropertyParser;
import cz.inqool.eas.common.differ.parser.DifferPropertyParser;

import java.lang.annotation.*;

/**
 * Declares that ATTRIBUTE or METHOD of the entity shall be diffed.
 *
 * @see DiffedClass JavaDoc
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface DiffedProperty {

    /**
     * Overrides name associated with this property.
     *
     * DefaultDifferPropertyParser converts getter name to property (getSummary -> summary, isWhite -> white)
     */
    String name() default "";

    Class<? extends DifferPropertyParser> parser() default DefaultDifferPropertyParser.class;

}
