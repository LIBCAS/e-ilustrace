package cz.inqool.eas.common.state.app;

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
 * Configuration for app state subsystem.
 *
 * If application wants to use app state subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class AppStateConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link AppStateApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for app_state_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("app-state.url", appStateUrl());

        MapPropertySource propertySource = new MapPropertySource("app_state_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link AppStateStore} bean.
     */
    @Bean
    public AppStateStore appStateStore() {
        return new AppStateStore();
    }

    /**
     * Constructs {@link AppStateService} bean.
     */
    @Bean
    public AppStateService appStateService() {
        return new AppStateService();
    }

    /**
     * Constructs {@link AppStateApi} bean.
     */
    @Bean
    public AppStateApi appStateApi() {
        return new AppStateApi();
    }

    /**
     * Returns url path of {@link AppStateApi}.
     */
    protected abstract String appStateUrl();
}
