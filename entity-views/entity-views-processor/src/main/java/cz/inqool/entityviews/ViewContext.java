package cz.inqool.entityviews;

import cz.inqool.entityviews.model.type.TypeModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ViewContext {
    private String name;
    private TypeModel type;
    private boolean useRef;
    private boolean useOneWay;
    private String oneWayTarget;

    public static ViewContext EMPTY_VIEW = new ViewContext(null, null, false, false, null);

    public boolean shouldSkip() {
        return name != null && name.equals(ViewableMapping.SKIP);
    }
}
