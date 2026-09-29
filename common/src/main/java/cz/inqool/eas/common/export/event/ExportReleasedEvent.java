package cz.inqool.eas.common.export.event;

import cz.inqool.eas.common.export.request.ExportRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event fired when {@link ExportRequest} is released back.
 */
public class ExportReleasedEvent extends ApplicationEvent {
    @Getter
    private final ExportRequest payload;

    public ExportReleasedEvent(Object source, ExportRequest payload) {
        super(source);
        this.payload = payload;
    }
}
