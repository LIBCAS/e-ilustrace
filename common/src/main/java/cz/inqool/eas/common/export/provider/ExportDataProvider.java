package cz.inqool.eas.common.export.provider;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

public enum ExportDataProvider implements Labeled {
    CONFIGURATION_PROVIDER("EAS Configuration data provider"),
    NOOP_PROVIDER("No-op provider"),
    PARAMS_PROVIDER("EAS Params provider"),
    REPORTING_PROVIDER("EAS Reporting provider"),
    SINGLE_ENTITY_PROVIDER("EAS Single entity provider");

    @Getter
    private final String label;

    ExportDataProvider(String label) {
        this.label = label;
    }

    @Override
    public String getId() {
        return name();
    }
}
