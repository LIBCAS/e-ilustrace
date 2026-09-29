package cz.inqool.eas.common.reporting.report;

import lombok.Data;

import java.util.Objects;

@Data
public class ReportColumn {
    private String name;
    private String datakey;
    private int width;
    private int minWidth;
    private ReportColumnType type;

    public ReportColumn() {
    }

    public ReportColumn(String name, String datakey, int width, int minWidth, ReportColumnType type) {
        this.name = name;
        this.datakey = datakey;
        this.width = width;
        this.minWidth = minWidth;
        this.type = type;
    }

    public ReportColumn(String name, String datakey, int width, int minWidth) {
        this(name, datakey, width, minWidth, ReportColumnType.TEXT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportColumn that = (ReportColumn) o;
        return datakey.equals(that.datakey) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(datakey, type);
    }
}
