package cz.inqool.eas.common.reporting.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectReportInputField extends ReportInputField {
    private List<SelectItem> selectItems;
    private boolean multiple;

    public SelectReportInputField(String name, String label, List<SelectItem> selectItems, boolean multiple) {
        super(name, label, ReportInputFieldType.SELECT);
        this.selectItems = selectItems;
        this.multiple = multiple;
    }

    @AllArgsConstructor
    @Data
    public static class SelectItem {
        String id;
        String name;
    }
}
