package cz.inqool.eas.common.differ.model.prop;

import cz.inqool.eas.common.differ.exception.DifferException;
import cz.inqool.eas.common.differ.strategy.ComparingStrategy;
import cz.inqool.eas.common.utils.ReflectionUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

public class DifferMethod extends DifferProperty<Method> {

    public DifferMethod(String propertyName, Method method, Class<?> residencyClazz, ComparingStrategy strategy, @Nullable DifferProperty<?> parentField) {
        super(propertyName, method, strategy, residencyClazz, parentField);
    }

    @Override
    public Class<?> propertyClass(Method method) {
        return ReflectionUtils.resolveType(method);
    }

    @Nullable
    @Override
    public Object getValue(@Nullable Object entity) {
        if (entity == null) {
            return null;
        }

        // javaProperty = method
        javaProperty.setAccessible(true);

        try {
            return org.springframework.util.ReflectionUtils.invokeMethod(javaProperty, entity);
        } catch (Exception e) {
            throw new DifferException("Method invocation failed", e)
                    .debugInfo(info -> info
                            .property("ancestryPath", super.ancestryPath)
                            .property("property", this.toString())
                            .property("method", javaProperty)
                            .property("methodName", javaProperty.getName())
                            .property("entity", entity));
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s()", super.toString(), javaProperty.getName());
    }
}
