package cz.inqool.eas.common.differ.model.prop;

import cz.inqool.eas.common.differ.exception.DifferException;
import cz.inqool.eas.common.differ.strategy.ComparingStrategy;
import cz.inqool.eas.common.utils.ReflectionUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;

public class DifferAttribute extends DifferProperty<Field> {

    public DifferAttribute(String propertyName, Field javaProperty, Class<?> residencyClazz, ComparingStrategy strategy, @Nullable DifferProperty<?> parentField) {
        super(propertyName, javaProperty, strategy, residencyClazz, parentField);
    }

    @Override
    public Class<?> propertyClass(Field field) {
        return ReflectionUtils.resolveType(field);
    }

    @Nullable
    @Override
    public Object getValue(@Nullable Object entity) throws DifferException {
        if (entity == null) {
            return null;
        }

        // javaProperty == field
        javaProperty.setAccessible(true);

        try {
            return javaProperty.get(entity);
        } catch (Exception e) { // should not happen
            throw new DifferException("Attribute value retrieval failed", e)
                    .debugInfo(info -> info
                            .property("ancestryPath", super.ancestryPath)
                            .property("property", this.toString())
                            .property("attribute", javaProperty)
                            .property("entity", entity)
                    );
        }
    }

}
