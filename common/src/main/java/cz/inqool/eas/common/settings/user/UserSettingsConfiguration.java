package cz.inqool.eas.common.settings.user;

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
 * Configuration for user settings subsystem.
 *
 * If application wants to use user settings subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class UserSettingsConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link UserSettingsApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for user_settings_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("user-settings.url", userSettingsUrl());

        MapPropertySource propertySource = new MapPropertySource("user_settings_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link UserSettingsStore} bean.
     */
    @Bean
    public UserSettingsStore userSettingsStore() {
        return new UserSettingsStore();
    }

    /**
     * Constructs {@link UserSettingsService} bean.
     */
    @Bean
    public UserSettingsService userSettingsService() {
        return new UserSettingsService();
    }

    /**
     * Constructs {@link UserSettingsApi} bean.
     */
    @Bean
    public UserSettingsApi userSettingsApi() {
        return new UserSettingsApi();
    }

    /**
     * Returns url path of {@link UserSettingsApi}.
     */
    protected abstract String userSettingsUrl();
}
