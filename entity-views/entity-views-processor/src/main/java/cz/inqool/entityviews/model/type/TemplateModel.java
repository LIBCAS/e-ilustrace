package cz.inqool.entityviews.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TemplateModel implements TypeModel {
    private String name;
    private TypeModel base;

    public String getDefinition() {
        if (Object.class.getCanonicalName().equals(base.getFullName())) {
            return name;
        } else {
            return name + " extends " + base.getUsage();
        }
    }

    public String getUsage() {
        return name;
    }

    @Override
    public boolean isUsedView() {
        return false;
    }

    @Override
    public String getFullName() {
        return name;
    }

    @Override
    public String getFullViewName() {
        return null;
    }

    @Override
    public String getRefType() {
        return name;
    }

    @Override
    public TypeModel[] getArguments() {
        return new TypeModel[0];
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getCollectionImplementation() {
        return null;
    }

    @Override
    public String getArgumentsDefinition() {
        return "";
    }

    @Override
    public String getArgumentsUsage() {
        return "";
    }
}
