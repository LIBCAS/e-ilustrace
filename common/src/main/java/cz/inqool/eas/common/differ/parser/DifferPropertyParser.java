package cz.inqool.eas.common.differ.parser;

import cz.inqool.eas.common.differ.model.prop.DifferProperty;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

// implement and declare as bean
public interface DifferPropertyParser {

    /**
     * Parse java property into differ field.
     *
     * @param property       java ATTRIBUTE {@link Field} or METHOD {@link Method}
     * @param residencyClazz class in which the property resides
     * @return Parsed differ field
     */
    DifferProperty<?> parse(AccessibleObject property, Class<?> residencyClazz);

}
