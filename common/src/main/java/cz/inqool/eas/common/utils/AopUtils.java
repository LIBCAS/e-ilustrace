package cz.inqool.eas.common.utils;

import org.springframework.aop.framework.Advised;

import java.util.Objects;

/**
 * Utility methods for working with proxy objects.
 */
public class AopUtils {

    /**
     * Checks whether the given object is a JDK dynamic proxy or a CGLIB proxy.
     *
     * @see org.springframework.aop.support.AopUtils#isAopProxy(Object)
     *
     * @param a Object to check
     */
    public static boolean isProxy(Object a) {
        return (org.springframework.aop.support.AopUtils.isAopProxy(a) && a instanceof Advised);
    }

    /**
     * Extracts exposed instance from a given proxy object or the object in case of raw object.
     *
     * @param a Possibly proxy object
     * @param <T> Type of provided object
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(T a) {
        if (isProxy(a)) {
            try {
                Object target = ((Advised) a).getTargetSource().getTarget();

                if (!Objects.equals(target, null)) {
                    return (T) target;
                } else {
                    return null;
                }
            } catch (Exception ignored) {
                return null; // return null if not in scope
            }
        } else {
            return a;
        }
    }
}
