package cz.inqool.eas.common.reporting.dto;

import cz.inqool.eas.common.reporting.input.ReportInputField;
import lombok.Data;

import java.util.List;

@Data
public class ReportDefinition {
    private String id;
    private String label;
    private String groupId;
    private String groupLabel;
    private List<ReportInputField> inputFields;
    private int order;
    private boolean autogenerate;
    private final boolean dashboardSupport;
    private final boolean skipLoading;

    public ReportDefinition(String id, String label, String groupId, String groupLabel, List<ReportInputField> inputFields, int order, boolean autogenerate, boolean dashboardSupport) {
        this.id = id;
        this.label = label;
        this.groupId = groupId;
        this.groupLabel = groupLabel;
        this.inputFields = inputFields;
        this.order = order;
        this.autogenerate = autogenerate;
        this.dashboardSupport = dashboardSupport;
        this.skipLoading = false;
    }

    public ReportDefinition(String id, String label, String groupId, String groupLabel, List<ReportInputField> inputFields, int order, boolean autogenerate, boolean dashboardSupport, boolean skipLoading) {
        this.id = id;
        this.label = label;
        this.groupId = groupId;
        this.groupLabel = groupLabel;
        this.inputFields = inputFields;
        this.order = order;
        this.autogenerate = autogenerate;
        this.dashboardSupport = dashboardSupport;
        this.skipLoading = skipLoading;
    }
}
