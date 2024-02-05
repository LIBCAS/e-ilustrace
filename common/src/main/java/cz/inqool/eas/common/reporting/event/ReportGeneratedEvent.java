package cz.inqool.eas.common.reporting.event;

import cz.inqool.eas.common.reporting.report.Report;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event fired when an report is generated.
 */
public class ReportGeneratedEvent extends ApplicationEvent {
    @Getter
    private final Report report;

    public ReportGeneratedEvent(Object service, Report report) {
        super(service);

        this.report = report;
    }
}
