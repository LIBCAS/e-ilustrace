package cz.inqool.entityviews.model.annotation;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Attribute.Array;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type.ClassType;
import cz.inqool.entityviews.context.Context;
import cz.inqool.entityviews.context.ContextHolder;
import cz.inqool.entityviews.model.AnnotationModel;
import cz.inqool.entityviews.model.type.TypeModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static cz.inqool.entityviews.ElementValueUtils.getElementValue;

@Getter
@Setter
public class SwaggerSchemaAnnotationModel extends AnnotationModel {

    private ClassType[] oneOf;

    public SwaggerSchemaAnnotationModel(TypeModel type, ClassType[] oneOf, Map<String, Object> attributes, String[] views) {
        super(type, attributes, views);
        this.oneOf = oneOf;
    }


    public static SwaggerSchemaAnnotationModel of(TypeModel typeModel, Map<String, Object> attributes, Map<MethodSymbol, Attribute> elementValues, String[] views) {
        Array oneOfArray = getElementValue("oneOf", elementValues, attribute -> (Array) attribute);
        ClassType[] oneOf = oneOfArray.getValue().stream()
                .map(attribute -> (Attribute.Class) attribute)
                .map(Attribute.Class::getValue)
                .map(type -> (ClassType) type)
                .toArray(ClassType[]::new);

        return new SwaggerSchemaAnnotationModel(typeModel, oneOf, attributes, views);
    }


    @Override
    protected String toString(Map.Entry<String, Object> entry) {
        if (entry.getKey().equals("oneOf")) {
            Context context = ContextHolder.getContext();
            String viewName = context.getView().getName();

            if (viewName != null) {
                String oneOfArray = Arrays.stream(oneOf)
                        .map(ClassType::toString)
                        .map(className -> className + LOWER_UNDERSCORE.to(UPPER_CAMEL, viewName) + ".class")
                        .collect(Collectors.joining(", ", "{", "}"));

                return entry.getKey() + " = " + oneOfArray;
            }
        }

        return super.toString(entry);
    }
}
