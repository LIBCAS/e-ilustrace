package cz.inqool.entityviews.model;

import cz.inqool.entityviews.ViewContext;
import cz.inqool.entityviews.function.Viewable;
import cz.inqool.entityviews.model.type.TypeModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter
public class AnnotationModel implements Viewable {
    private TypeModel type;

    private Map<String, Object> attributes;

    private String[] views;

    /**
     * View mapping is not supported.
     */
    private final Map<String, ViewContext> viewMappings = Collections.emptyMap();

    public String toString() {
        String strAttributes = attributes.
                entrySet().
                stream().
                map(this::toString).
                collect(Collectors.joining(", "));

        return "@" + type.getFullName() + "(" + strAttributes + ")";
    }

    protected String toString(Map.Entry<String, Object> entry) {
        return entry.getKey() + " = " + entry.getValue();
    }

    public static String escapeString(String str) {
        if (str == null) {
            return null;
        }

        return "\"" + str + "\"";
    }
}
