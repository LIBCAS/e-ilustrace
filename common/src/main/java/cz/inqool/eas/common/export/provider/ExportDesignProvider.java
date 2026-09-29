package cz.inqool.eas.common.export.provider;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

public enum ExportDesignProvider implements Labeled {
    DOCX_PROVIDER("Simple docx4j design provider"),
    DYNAMIC_PROVIDER("Dynamic grid design provider"),
    SIMPLE_PROVIDER("Simple design provider");

    @Getter
    private final String label;

    ExportDesignProvider(String label) {
        this.label = label;
    }

    @Override
    public String getId() {
        return name();
    }
}
