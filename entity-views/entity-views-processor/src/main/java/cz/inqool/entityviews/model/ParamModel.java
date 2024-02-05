package cz.inqool.entityviews.model;

import cz.inqool.entityviews.model.type.TypeModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ParamModel {
    private String name;
    private TypeModel type;

    public String getDefinition() {
        return type.getUsage() + " " + name;
    }
}
