package cz.inqool.eas.common.action;

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
 * Configuration for backend action subsystem.
 * <p>
 * If application wants to use action subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class ActionConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link ActionApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for action_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("action.url", actionUrl());

        MapPropertySource propertySource = new MapPropertySource("action_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link ActionRepository} bean.
     */
    @Bean
    public ActionRepository actionRepository() {
        return new ActionRepository();
    }

    /**
     * Constructs {@link ActionService} bean.
     */
    @Bean
    public ActionService actionService() {
        return new ActionService();
    }

    /**
     * Constructs {@link ActionApi} bean.
     */
    @Bean
    public ActionApi actionApi() {
        return new ActionApi();
    }


    /**
     * Returns url path of {@link ActionApi}.
     */
    protected abstract String actionUrl();
}
