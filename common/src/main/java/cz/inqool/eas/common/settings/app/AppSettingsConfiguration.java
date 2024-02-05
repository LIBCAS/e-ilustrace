package cz.inqool.eas.common.settings.app;

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
 * Configuration for app settings subsystem.
 *
 * If application wants to use app settings subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class AppSettingsConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link AppSettingsApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for app_settings_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("app-settings.url", appSettingsUrl());

        MapPropertySource propertySource = new MapPropertySource("app_settings_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link AppSettingsStore} bean.
     */
    @Bean
    public AppSettingsStore appSettingsStore() {
        return new AppSettingsStore();
    }

    /**
     * Constructs {@link AppSettingsService} bean.
     */
    @Bean
    public AppSettingsService appSettingsService() {
        return new AppSettingsService();
    }

    /**
     * Constructs {@link AppSettingsApi} bean.
     */
    @Bean
    public AppSettingsApi appSettingsApi() {
        return new AppSettingsApi();
    }

    /**
     * Returns url path of {@link AppSettingsApi}.
     */
    protected abstract String appSettingsUrl();
}
