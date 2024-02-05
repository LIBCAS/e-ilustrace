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

import javax.persistence.ManyToOne;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static cz.inqool.entityviews.ElementValueUtils.getElementValue;

/**
 * @see ManyToOne
 */
@Getter
@Setter
public class HibernateManyToOneAnnotationModel extends AnnotationModel {

    private ClassType targetEntity;

    public HibernateManyToOneAnnotationModel(TypeModel type, ClassType targetEntity, Map<String, Object> attributes, String[] views) {
        super(type, attributes, views);

        this.targetEntity = targetEntity;
    }


    public static AnnotationModel of(TypeModel typeModel, Map<String, Object> attributes, Map<MethodSymbol, Attribute> elementValues, String[] views) {
        Attribute.Class targetEntity = getElementValue("targetEntity", elementValues, attribute -> (Attribute.Class) attribute);

        if (targetEntity.getValue() instanceof ClassType) {
            ClassType schema = (ClassType) targetEntity.getValue();
            return new HibernateManyToOneAnnotationModel(typeModel, schema, attributes, views);
        } else {
            return new AnnotationModel(typeModel, attributes, views);
        }
    }


    @Override
    protected String toString(Map.Entry<String, Object> entry) {
        if (entry.getKey().equals("targetEntity") && targetEntity != null) {
            Context context = ContextHolder.getContext();
            String viewName = context.getView().getName();

            if (viewName != null) { // replace only if view is present
                String className = targetEntity.toString();
                String valueClass = className + LOWER_UNDERSCORE.to(UPPER_CAMEL, viewName) + ".class";
                return entry.getKey() + " = " + valueClass;
            }
        }

        return super.toString(entry);
    }
}
