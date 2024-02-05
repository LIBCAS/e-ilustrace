package cz.inqool.eas.common.intl;

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
 * Configuration for intl subsystem.
 *
 * If application wants to use intl subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class IntlConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link TranslationApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for intl_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("intl.translation.url", translationUrl());

        MapPropertySource propertySource = new MapPropertySource("intl_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link TranslationRepository} bean.
     */
    @Bean
    public TranslationRepository translationRepository() {
        return new TranslationRepository();
    }

    /**
     * Constructs {@link TranslationService} bean.
     */
    @Bean
    public TranslationService translationService() {
        return new TranslationService();
    }

    /**
     * Constructs {@link TranslationApi} bean.
     */
    @Bean
    public TranslationApi translationApi() {
        return new TranslationApi();
    }

    /**
     * Returns url path of {@link TranslationApi}.
     */
    protected abstract String translationUrl();
}
