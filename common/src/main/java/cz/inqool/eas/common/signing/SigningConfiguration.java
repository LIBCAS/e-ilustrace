package cz.inqool.eas.common.signing;

import cz.inqool.eas.common.sequence.SequenceApi;
import cz.inqool.eas.common.sequence.SequenceGenerator;
import cz.inqool.eas.common.sequence.SequenceRepository;
import cz.inqool.eas.common.sequence.SequenceService;
import cz.inqool.eas.common.signing.request.SignRequestApi;
import cz.inqool.eas.common.signing.request.SignRequestRepository;
import cz.inqool.eas.common.signing.request.SignRequestService;
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
 * Configuration for signing subsystem.
 *
 * If application wants to use signing subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class SigningConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link SequenceApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for signing_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("signing.url", signingUrl());

        MapPropertySource propertySource = new MapPropertySource("signing_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link SignRequestRepository} bean.
     */
    @Bean
    public SignRequestRepository signRequestRepository() {
        return new SignRequestRepository();
    }

    /**
     * Constructs {@link SignRequestService} bean.
     */
    @Bean
    public SignRequestService signRequestService() {
        return new SignRequestService();
    }

    /**
     * Constructs {@link SignRequestApi} bean.
     */
    @Bean
    public SignRequestApi signRequestApi() {
        return new SignRequestApi();
    }

    /**
     * Returns url path of {@link SignRequestApi}.
     */
    protected abstract String signingUrl();
}
