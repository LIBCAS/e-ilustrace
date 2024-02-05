package cz.inqool.eas.common.export.batch;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

/**
 * Enumeration of possible states of a {@link ExportBatch}.
 */
public enum ExportBatchState implements Labeled {
    /**
     * State of a export batch being processed
     */
    PROCESSING("Zpracováva se"),

    /**
     * State of a export batch successfully processed and corresponding export file was generated
     * Currently packaging to zip.
     */
    FINALIZING("Finalizuje se"),

    /**
     * State of a export batch ready to download.
     */
    PROCESSED("Ukončen");

    @Getter
    private final String label;

    ExportBatchState(String label ) {
        this.label = label;
    }

    @Override
    public String getId() {
        return name();
    }
}
