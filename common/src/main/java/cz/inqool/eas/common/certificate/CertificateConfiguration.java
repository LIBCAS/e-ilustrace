package cz.inqool.eas.common.certificate;

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
 * Configuration for certificate subsystem.
 * <p>
 * If application wants to use certificate subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class CertificateConfiguration {
    @Autowired
    private ConfigurableEnvironment env;

    /**
     * Adds custom property source to spring for setting the url path of {@link CertificateApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for certificate_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("certificate.url", certificateUrl());

        MapPropertySource propertySource = new MapPropertySource("certificate_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link CertificateRepository} bean.
     */
    @Bean
    public CertificateRepository certificateRepository() {
        return new CertificateRepository();
    }

    /**
     * Constructs {@link CertificateService} bean.
     */
    @Bean
    public CertificateService certificateService() {
        return new CertificateService();
    }

    /**
     * Constructs {@link CertificateApi} bean.
     */
    @Bean
    public CertificateApi certificateApi() {
        return new CertificateApi();
    }


    /**
     * Returns url path of {@link CertificateApi}.
     */
    protected abstract String certificateUrl();
}
