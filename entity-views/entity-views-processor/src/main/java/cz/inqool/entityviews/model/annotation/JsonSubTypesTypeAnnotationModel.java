package cz.inqool.entityviews.model.annotation;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type.ClassType;
import cz.inqool.entityviews.context.Context;
import cz.inqool.entityviews.context.ContextHolder;
import cz.inqool.entityviews.model.AnnotationModel;
import cz.inqool.entityviews.model.type.TypeModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static cz.inqool.entityviews.ElementValueUtils.getElementValue;

@Getter
@Setter
public class JsonSubTypesTypeAnnotationModel extends AnnotationModel {

    private ClassType value;

    public JsonSubTypesTypeAnnotationModel(TypeModel type, ClassType value, Map<String, Object> attributes, String[] views) {
        super(type, attributes, views);

        this.value = value;
    }


    public static JsonSubTypesTypeAnnotationModel of(TypeModel typeModel, Map<String, Object> attributes, Map<MethodSymbol, Attribute> elementValues, String[] views) {
        ClassType value = (ClassType) getElementValue("value", elementValues, attribute -> (Attribute.Class) attribute).getValue();

        return new JsonSubTypesTypeAnnotationModel(typeModel, value, attributes, views);
    }


    @Override
    protected String toString(Map.Entry<String, Object> entry) {
        if (entry.getKey().equals("value")) {
            Context context = ContextHolder.getContext();
            String viewName = context.getView().getName();

            if (viewName != null) { // replace only if view is present
                String className = value.toString();
                String valueClass = className + LOWER_UNDERSCORE.to(UPPER_CAMEL, viewName) + ".class";
                return entry.getKey() + " = " + valueClass;
            }
        }

        return super.toString(entry);
    }
}
