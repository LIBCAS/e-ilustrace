package cz.inqool.entityviews.model;

import cz.inqool.entityviews.ViewContext;
import cz.inqool.entityviews.function.Viewable;
import cz.inqool.entityviews.model.type.TypeModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class ImplementModel implements Viewable {
    private TypeModel implementedInterface;

    private String[] views;

    /**
     * View mapping is not supported.
     */
    private final Map<String, ViewContext> viewMappings = Collections.emptyMap();
}
