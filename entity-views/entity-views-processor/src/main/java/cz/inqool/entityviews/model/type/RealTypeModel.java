package cz.inqool.entityviews.model.type;

import com.google.common.base.CaseFormat;
import cz.inqool.entityviews.ViewContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

import static cz.inqool.entityviews.context.ContextHolder.getContext;

@AllArgsConstructor
@Getter
@Setter
public class RealTypeModel implements TypeModel {
    private String packageName;
    private String className;
    private TypeModel[] arguments;

    public boolean isUsedView() {
        ViewContext view = getContext().getView();
        return this.equals(view.getType());
    }

    public String getDefinition() {
        String arguments = hasArguments() ? "<" + getArgumentsDefinition() + ">" : "";
        return getViewName() + arguments;
    }

    public String getUsage() {
        boolean useShortName = getContext().getUnit().getClazz().getType().equals(this);
        boolean useRef = getContext().getView().isUseRef();

        String arguments = hasArguments() ? "<" + getArgumentsUsage() + ">" : "";

        if (isUsedView()) {
            if (useRef) {
                if (useShortName) {
                    return getRefName();
                } else {
                    return getFullRefName();
                }
            } else {
                if (useShortName) {
                    return getViewName() + arguments;
                } else {
                    return getFullViewName() + arguments;
                }
            }

        }
        if (Object.class.getPackageName().equals(packageName) || useShortName) {
            return className + arguments;
        } else {
            return packageName != null ? packageName + "." + className + arguments : className + arguments;
        }
    }

    public String getUsageOriginal() {
        boolean useShortName = getContext().getUnit().getClazz().getType().equals(this);

        String arguments = hasArguments() ? "<" + getArgumentsUsage() + ">" : "";

        if (Object.class.getPackageName().equals(packageName) || useShortName) {
            return className + arguments;
        } else {
            return packageName != null ? packageName + "." + className + arguments : className + arguments;
        }
    }

    public boolean hasArguments() {
        return arguments.length > 0;
    }

    public String getRefType() {
        String refClassName = className + "Ref";
        return packageName != null ? packageName + "." + refClassName : refClassName;
    }

    public String getArgumentsUsage() {
        return Arrays.
                stream(arguments).
                map(TypeModel::getUsage).
                collect(Collectors.joining(", "));
    }

    public String getArgumentsDefinition() {
        return Arrays.
                stream(arguments).
                map(TypeModel::getDefinition).
                collect(Collectors.joining(", "));
    }

    public String getViewName() {
        ViewContext view = getContext().getView();

        if (view.getName() == null) {
            return className;
        }

        return className + toPascalCase(view.getName());
    }

    public String getFullViewName() {
        String viewName = getViewName();
        return packageName != null ? packageName + "." + viewName : viewName;
    }

    public String getRefName() {
        return className + "Ref";
    }

    public String getFullRefName() {
        String refName = getRefName();
        return packageName != null ? packageName + "." + refName : refName;
    }

    public String getFullName() {
        return packageName != null ? packageName + "." + className : className;
    }

    public boolean isCollection() {
        return isSet() || isList();
    }

    public String getCollectionImplementation() {
        String fullName = getFullName();

        if (fullName.equals(ArrayList.class.getCanonicalName()) ||
                fullName.equals(LinkedList.class.getCanonicalName()) ||
                fullName.equals(HashSet.class.getCanonicalName()) ||
                fullName.equals(LinkedHashSet.class.getCanonicalName()) ||
                fullName.equals(TreeSet.class.getCanonicalName())) {
            return fullName;
        } else if (fullName.equals(List.class.getCanonicalName())) {
            return ArrayList.class.getCanonicalName();
        } else if (fullName.equals(Set.class.getCanonicalName())) {
            return LinkedHashSet.class.getCanonicalName();
        } else {
            return null;
        }
    }

    private boolean isList() {
        String fullName = getFullName();

        return fullName.equals(List.class.getCanonicalName()) ||
                fullName.equals(ArrayList.class.getCanonicalName()) ||
                fullName.equals(LinkedList.class.getCanonicalName());
    }

    private boolean isSet() {
        String fullName = getFullName();

        return fullName.equals(Set.class.getCanonicalName()) ||
                fullName.equals(HashSet.class.getCanonicalName()) ||
                fullName.equals(LinkedHashSet.class.getCanonicalName()) ||
                fullName.equals(TreeSet.class.getCanonicalName());
    }

    public static String toPascalCase(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str);
    }

    public static String toSnakeCase(String str) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RealTypeModel typeModel = (RealTypeModel) o;
        return Objects.equals(packageName, typeModel.packageName) &&
                className.equals(typeModel.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, className);
    }
}
