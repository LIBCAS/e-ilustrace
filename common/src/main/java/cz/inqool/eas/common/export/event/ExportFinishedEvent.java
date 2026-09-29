package cz.inqool.eas.common.export.event;

import cz.inqool.eas.common.export.request.ExportRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event fired when {@link ExportRequest} is finished.
 */
public class ExportFinishedEvent extends ApplicationEvent {
    @Getter
    private final ExportRequest payload;

    public ExportFinishedEvent(Object source, ExportRequest payload) {
        super(source);
        this.payload = payload;
    }
}
