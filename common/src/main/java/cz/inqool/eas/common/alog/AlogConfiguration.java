package cz.inqool.eas.common.alog;

import cz.inqool.eas.common.alog.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for audit logging subsystem.
 *
 * If application wants to use audit logging subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class AlogConfiguration {
    @Autowired
    private ConfigurableEnvironment env;

    /**
     * Adds custom property source to spring for setting the url path of {@link EventApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for alog_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("alog.url", eventUrl());

        MapPropertySource propertySource = new MapPropertySource("alog_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }


    /**
     * Constructs {@link EventRepository} bean.
     */
    @Bean
    public EventRepository eventRepository() {
        return new EventRepository();
    }

    /**
     * Constructs {@link EventService} bean.
     */
    @Bean
    public EventService eventService() {
        EventService service = new EventService();
        service.setSource(getSource());

        return service;
    }

    /**
     * Constructs {@link EventApi} bean.
     */
    @Bean
    public EventApi eventApi() {
        return new EventApi();
    }

    /**
     * Constructs {@link EventBuilder} bean.
     */
    @Bean
    public EventBuilder eventBuilder() {
        return new EventBuilder();
    }

    /**
     * Constructs {@link AppEventHandler} bean.
     */
    @Bean
    public AppEventHandler appEventHandler() {
        return new AppEventHandler();
    }

    protected abstract String getSource();

    /**
     * Returns url path of {@link EventApi}.
     */
    protected abstract String eventUrl();

    /**
     * Custom access checker.
     */
    protected abstract AlogAccessChecker alogAccessChecker();
}
