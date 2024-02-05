package cz.inqool.eas.common.export.event;

import cz.inqool.eas.common.export.request.ExportRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event fired when {@link ExportRequest} is acquired by export runner.
 */
public class ExportAcquiredEvent extends ApplicationEvent {
    @Getter
    private final ExportRequest payload;

    public ExportAcquiredEvent(Object source, ExportRequest payload) {
        super(source);
        this.payload = payload;
    }
}
