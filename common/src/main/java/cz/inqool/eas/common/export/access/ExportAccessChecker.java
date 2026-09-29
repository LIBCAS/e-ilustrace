package cz.inqool.eas.common.export.access;

import cz.inqool.eas.common.export.template.ExportTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public interface ExportAccessChecker {
    AccessCheckResult checkAccess(ExportTemplate template);
    Map<String, Boolean> checkAccess(List<ExportTemplate> templates);

    @AllArgsConstructor
    @Getter
    @Setter
    public static class AccessCheckResult {
        boolean access;

        /**
         * Additional params.
         */
        protected String configuration;
    }
}
