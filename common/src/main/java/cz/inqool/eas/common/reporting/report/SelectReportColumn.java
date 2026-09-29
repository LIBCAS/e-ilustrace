package cz.inqool.eas.common.reporting.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectReportColumn extends ReportColumn {
    private List<SelectItem> selectItems;

    public SelectReportColumn(String name, String datakey, int width, int minWidth, List<SelectItem> selectItems) {
        super(name, datakey, width, minWidth, ReportColumnType.SELECT);
        this.selectItems = selectItems;
    }

    @AllArgsConstructor
    @Data
    public static class SelectItem {
        String id;
        String name;
    }
}
