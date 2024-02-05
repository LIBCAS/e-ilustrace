package cz.inqool.eas.common.alog.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PreDestroy;

public class AppEventHandler {
    private EventService service;

    private EventBuilder builder;

    @EventListener
    public void appStartup(ApplicationReadyEvent event) {
        service.create(builder.startup());
    }

    @PreDestroy
    public void appShutdown() {
        service.create(builder.shutdown());
    }

    @Autowired
    public void setService(EventService service) {
        this.service = service;
    }

    @Autowired
    public void setBuilder(EventBuilder builder) {
        this.builder = builder;
    }
}
