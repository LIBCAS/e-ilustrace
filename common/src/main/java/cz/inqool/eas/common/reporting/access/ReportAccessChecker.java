package cz.inqool.eas.common.reporting.access;

import cz.inqool.eas.common.reporting.dto.ReportDefinition;

import java.util.List;
import java.util.Map;

public interface ReportAccessChecker {
    boolean checkAccess(ReportDefinition definition);
    Map<String, Boolean> checkAccess(List<ReportDefinition> definition);
}
