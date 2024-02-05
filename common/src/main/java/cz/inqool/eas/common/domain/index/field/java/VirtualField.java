package cz.inqool.eas.common.domain.index.field.java;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Virtually constructed field for internal parsing.
 */
public class VirtualField implements Field {
    private final String name;
    private final Class<?> type;
    private final List<? extends Annotation> annotations;

    public VirtualField(String name, Class<?> type, List<Annotation> annotations) {
        this.name = name;
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> cls) {
        return (T) annotations.stream().filter(cls::isInstance).findFirst().orElse(null);
    }

    @Override
    public Class<?> resolveType() {
        return type;
    }
}
