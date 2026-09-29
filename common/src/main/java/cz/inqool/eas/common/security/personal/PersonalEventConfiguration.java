package cz.inqool.eas.common.security.personal;

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
 * Configuration for personal events subsystem.
 *
 * If application wants to use personal events subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class PersonalEventConfiguration {
    @Autowired
    private ConfigurableEnvironment env;

    /**
     * Adds custom property source to spring for setting the url path of {@link PersonalEventApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for personal_event_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("personalEvent.url", personalEventUrl());

        MapPropertySource propertySource = new MapPropertySource("personal_event_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link PersonalEventRepository} bean.
     */
    @Bean
    public PersonalEventRepository personalEventRepository() {
        return new PersonalEventRepository();
    }

    /**
     * Constructs {@link PersonalEventService} bean.
     */
    @Bean
    public PersonalEventService personalEventService() {
        return new PersonalEventService();
    }

    /**
     * Constructs {@link PersonalEventApi} bean.
     */
    @Bean
    public PersonalEventApi personalEventApi() {
        return new PersonalEventApi();
    }

    /**
     * Returns url path of {@link PersonalEventApi}.
     */
    protected abstract String personalEventUrl();
}
