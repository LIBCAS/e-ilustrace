package cz.inqool.eas.common.sequence;

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
 * Configuration for sequence subsystem.
 *
 * If application wants to use sequence subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class SequenceConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link SequenceApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for sequence_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("sequence.url", sequenceUrl());

        MapPropertySource propertySource = new MapPropertySource("sequence_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link SequenceRepository} bean.
     */
    @Bean
    public SequenceRepository sequenceRepository() {
        return new SequenceRepository();
    }

    /**
     * Constructs {@link SequenceGenerator} bean.
     */
    @Bean
    public SequenceGenerator sequenceGenerator() {
        return new SequenceGenerator();
    }

    /**
     * Constructs {@link SequenceService} bean.
     */
    @Bean
    public SequenceService sequenceService() {
        return new SequenceService();
    }

    /**
     * Constructs {@link SequenceApi} bean.
     */
    @Bean
    public SequenceApi sequenceApi() {
        return new SequenceApi();
    }

    /**
     * Returns url path of {@link SequenceApi}.
     */
    protected abstract String sequenceUrl();
}
