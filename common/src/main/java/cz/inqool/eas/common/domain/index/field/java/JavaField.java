package cz.inqool.eas.common.domain.index.field.java;

import cz.inqool.eas.common.utils.ReflectionUtils;

import java.lang.annotation.Annotation;

/**
 * Wrapper around java field for internal field parsing.
 */
public class JavaField implements Field {
    private final java.lang.reflect.Field field;

    public JavaField(java.lang.reflect.Field field) {
        this.field = field;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> cls) {
        return field.getAnnotation(cls);
    }

    @Override
    public Class<?> resolveType() {
        return ReflectionUtils.resolveType(field);
    }
}
