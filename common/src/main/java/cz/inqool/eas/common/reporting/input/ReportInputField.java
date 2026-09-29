package cz.inqool.eas.common.reporting.input;

import lombok.Data;

@Data
public class ReportInputField {
    private String name;
    private String label;
    private ReportInputFieldType type;

    public ReportInputField(String name, String label, ReportInputFieldType type) {
        this.name = name;
        this.label = label;
        this.type = type;
    }
}
