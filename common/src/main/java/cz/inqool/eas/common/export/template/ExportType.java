package cz.inqool.eas.common.export.template;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

@Getter
public enum ExportType implements Labeled {
    DOCX("Word", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", true),
    XLSX("Excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", false),
    PDF("PDF", "application/pdf", true),
    HTML("Html","text/html", false),
    XML("Xml", "text/xml", false),
    CSV("Csv", "text/csv", false);

    private final String label;
    private final String contentType;
    private final boolean pagination;

    ExportType(String label, String contentType, boolean pagination) {
        this.label = label;
        this.contentType = contentType;
        this.pagination = pagination;
    }

    public String getId() {
        return this.name();
    }
}
