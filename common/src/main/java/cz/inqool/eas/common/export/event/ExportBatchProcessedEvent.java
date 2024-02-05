package cz.inqool.eas.common.export.event;

import cz.inqool.eas.common.export.batch.ExportBatch;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event fired when {@link ExportBatch} is processed.
 */
public class ExportBatchProcessedEvent extends ApplicationEvent {
    @Getter
    private final ExportBatch payload;

    public ExportBatchProcessedEvent(Object source, ExportBatch payload) {
        super(source);
        this.payload = payload;
    }
}
