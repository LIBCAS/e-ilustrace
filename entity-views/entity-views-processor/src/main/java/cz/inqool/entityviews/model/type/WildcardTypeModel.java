package cz.inqool.entityviews.model.type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WildcardTypeModel extends RealTypeModel {

    private String kind;

    public WildcardTypeModel(RealTypeModel realTypeModel, String kind) {
        super(realTypeModel.getPackageName(), realTypeModel.getClassName(), realTypeModel.getArguments());
        this.kind = kind;
    }

    @Override
    public String getUsage() {
        return kind + super.getUsage();
    }

    public String getPlainUsage() {
        return super.getUsage();
    }
}
