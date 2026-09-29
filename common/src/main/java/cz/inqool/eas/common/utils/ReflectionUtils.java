package cz.inqool.eas.common.utils;

import org.reflections.Reflections;
import org.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Set;

public class ReflectionUtils {

    private static Reflections REFLECTIONS;


    /**
     * Returns project classes reflections (in package "cz.inqool.peva")
     */
    private static Reflections getReflections() {
        if (REFLECTIONS == null) {
            REFLECTIONS = new Reflections("cz.inqool");
        }
        return REFLECTIONS;
    }

    public static <T> Set<Class<? extends T>> getSubTypesOf(Class<T> superClass) {
        return getReflections().getSubTypesOf(superClass);
    }

    public static Class<?> resolveType(java.lang.reflect.Method method) {
        // resolve method's RETURN type
        return resolveType(method.getReturnType(), method.getGenericReturnType());
    }

    public static Class<?> resolveType(java.lang.reflect.Field field) {
        return resolveType(field.getType(), field.getGenericType());
    }

    public static boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    public static boolean doesTypeImplementInterface(Class<?> type, Class<?> interfaceClass) {
        Set<Class<?>> allInterfacesAsSet = ClassUtils.getAllInterfacesForClassAsSet(type);
        return allInterfacesAsSet.stream().anyMatch(i -> i.equals(interfaceClass));
    }

    /**
     * Resolve provided type.
     *
     * If provided type is a collection or an array, resolve type within {@code Collection<MyEntity> -> MyEntity}
     */
    private static Class<?> resolveType(Class<?> type, Type genericType) {
        if (isCollection(type)) {
            Object unresolvedType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            if (unresolvedType instanceof TypeVariable) {
                return (Class<?>) ((TypeVariable<?>) unresolvedType).getBounds()[0];
            } else if (unresolvedType instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) unresolvedType).getRawType();
            } else if (unresolvedType instanceof WildcardType) { // Set<? extends MyEntity> -> return Class<MyEntity>
                return (Class<?>) ((WildcardType) unresolvedType).getUpperBounds()[0];
            } else {
                return (Class<?>) unresolvedType;
            }
        } else if (type.isArray()) {
            return type.getComponentType();
        } else {
            return type;
        }
    }
}
