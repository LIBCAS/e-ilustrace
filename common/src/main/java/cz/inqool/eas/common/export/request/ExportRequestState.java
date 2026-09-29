package cz.inqool.eas.common.export.request;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

/**
 * Enumeration of possible states of a {@link ExportRequest}.
 */
public enum ExportRequestState implements Labeled {
    /**
     * State of a export request after creation
     */
    PENDING("Čekající"),

    /**
     * State of a export request being processed
     */
    PROCESSING("Zpracováva se"),

    /**
     * State of a export successfully processed and corresponding export file was generated
     */
    PROCESSED("Ukončen"),

    /**
     * State of a export request whose processing failed
     */
    FAILED("Ukončen s chybu");

    @Getter
    private final String label;

    ExportRequestState(String label ) {
        this.label = label;
    }

    @Override
    public String getId() {
        return name();
    }
}
