package cz.inqool.eas.common.settings.named;

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
 * Configuration for named settings subsystem.
 *
 * If application wants to use user settings subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class NamedSettingsConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link NamedSettingsApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for named_settings_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("named-settings.url", namedSettingsUrl());

        MapPropertySource propertySource = new MapPropertySource("named_settings_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link NamedSettingsStore} bean.
     */
    @Bean
    public NamedSettingsStore namedSettingsStore() {
        return new NamedSettingsStore();
    }

    /**
     * Constructs {@link NamedSettingsService} bean.
     */
    @Bean
    public NamedSettingsService namedSettingsService() {
        return new NamedSettingsService();
    }

    /**
     * Constructs {@link NamedSettingsApi} bean.
     */
    @Bean
    public NamedSettingsApi namedSettingsApi() {
        return new NamedSettingsApi();
    }

    /**
     * Returns url path of {@link NamedSettingsApi}.
     */
    protected abstract String namedSettingsUrl();
}
