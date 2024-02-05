package cz.inqool.eas.common.domain.index.field.java;

import java.lang.annotation.Annotation;

/**
 * Contract for internal field parsing.
 */
public interface Field {
    String getName();
    <T extends Annotation> T getAnnotation(Class<T> cls);
    Class<?> resolveType();
}
