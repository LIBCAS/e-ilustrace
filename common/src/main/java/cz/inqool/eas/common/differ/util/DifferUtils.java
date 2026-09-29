package cz.inqool.eas.common.differ.util;

import cz.inqool.eas.common.differ.annotation.DiffedProperty;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class DifferUtils {

    /**
     * Field that is neither static nor final is considered an attribute.
     *
     * Such check prevents constants as
     *
     * `public static final MY_VIEW_NAME = "my_view_name"`
     *
     * to be part of parsed fields.
     */
    public static boolean fieldIsAttribute(Field field) {
        int mods = field.getModifiers();
        return !Modifier.isStatic(mods) || !Modifier.isFinal(mods);
    }

    public static boolean methodHasGetterDeclaration(Method method) {
        return method.getParameterCount() == 0 && !method.getReturnType().equals(Void.TYPE);
    }

    public static boolean methodNameStartsAsGetter(Method method) {
        return method.getName().startsWith("get") || method.getName().startsWith("is");
    }

    public static boolean methodDeclaringClassIsNotObject(Method method) {
        // ignore methods from Object class
        return !method.getDeclaringClass().equals(Object.class);
    }

    public static String propertyName(@Nullable DiffedProperty annotation, @NonNull String defaultName) {
        // field might not have an annotation (if declared via DiffedClass or during recursive processing of @Embedded)
        if (annotation != null && !annotation.name().isBlank()) {
            return annotation.name();
        }

        return defaultName;
    }

    public static String methodNameToProperty(@NonNull Method method) {
        String name = method.getName();

        if (name.startsWith("get") && name.length() > 3) { // getSummary -> summary
            String withoutGetPrefix = name.substring(3);
            return StringUtils.uncapitalize(withoutGetPrefix);
        }
        if (name.startsWith("is") && name.length() > 2) { // isOriginal -> original
            String withoutIsPrefix = name.substring(2);
            return StringUtils.uncapitalize(withoutIsPrefix);
        }

        return name;
    }

}
