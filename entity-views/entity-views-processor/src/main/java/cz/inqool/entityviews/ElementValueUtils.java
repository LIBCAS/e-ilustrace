package cz.inqool.entityviews;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.model.JavacElements;

import javax.lang.model.element.AnnotationMirror;
import java.util.Map;
import java.util.function.Function;

public class ElementValueUtils {

    /**
     * Return annotation element value.
     *
     * @param name          name of annotation element
     * @param elementValues map of all annotation elements (usually obtained by
     *                      {@link JavacElements#getElementValuesWithDefaults(AnnotationMirror)})
     * @param mapper        mapping function for the found element value. Usually used to cast the value to the proper
     *                      type. Use {@link Function#identity()} when not needed.
     * @param <T>           type of element value returned
     * @return found annotation element value matching given {@code name} or {@code null} of none matches
     */
    public static <T> T getElementValue(String name, Map<MethodSymbol, Attribute> elementValues, Function<Attribute, T> mapper) {
        return elementValues.entrySet().stream()
                .filter(entry -> entry.getKey().getSimpleName().toString().equals(name))
                .findAny()
                .map(Map.Entry::getValue)
                .map(mapper)
                .orElse(null);
    }
}
