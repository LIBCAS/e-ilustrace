package cz.inqool.eas.common.script;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

public enum ScriptType implements Labeled {
    GROOVY("Groovy"),
    JAVASCRIPT("Javascript");

    @Getter
    private final String label;

    ScriptType(String label ) {
        this.label = label;
    }

    @Override
    public String getId() {
        return name();
    }
}
