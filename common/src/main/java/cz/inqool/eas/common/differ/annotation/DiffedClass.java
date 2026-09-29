package cz.inqool.eas.common.differ.annotation;

import cz.inqool.eas.common.differ.parser.DefaultDifferPropertyParser;
import cz.inqool.eas.common.differ.parser.DifferPropertyParser;

import java.lang.annotation.*;

/**
 * Marks an entity to be diffed.
 *
 * DEFAULT BEHAVIOUR: All attributes and all methods are marked to be diffed.
 * Override default behaviour with annotation methods if needed or with {@link DiffedIgnore} / {@link DiffedProperty}.
 *
 * <pre>
 * Fields evaluation process:
 * 1. Add ALL fields of an entity (including attributes of superclass) into SET.
 *
 * 2. If {@link #properties} is not empty, then ONLY these declared fields are retained in the SET.
 *
 * 3. If {@link #ignoreProperties} is not empty, then these declared fields are removed from the SET.
 *
 * 4. ALL fields are searched for explicit {@link DiffedProperty}, if annotation is present,
 *    then fields are added into the SET
 *    (overriding behaviour from step 1 or 2. or possibly back-inserting after being removed in step 3)
 *
 * 5. ALL fields are searched for explicit {@link DiffedIgnore}, if annotation is present,
 *    then fields are removed from the SET.
 *    (possibly overriding step 2 or 4)
 * </pre>
 *
 * Super-Sub Class behaviour:
 *
 * This annotation is {@link Inherited}, therefore subclasses inherit behaviour of annotation from superclass.
 * If {@link DiffedClass} is declared in subclass, it fully overrides annotation declared in superclass.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface DiffedClass {

    /**
     * Property names for auto-diffing
     *
     * Use for explicit declaration of properties to override default behaviour.
     */
    String[] properties() default {};

    /**
     * Property names for declaring that diffing should be ignored for declared fields.
     *
     * Can be used to override default behaviour of {@link DiffedClass}.
     *
     * Property declared both in {@link #properties} and {@link #ignoreProperties} is ignored,
     * as this method has higher priority in logical evaluation.
     */
    String[] ignoreProperties() default {};

    Class<? extends DifferPropertyParser> parser() default DefaultDifferPropertyParser.class;

}
