package cz.inqool.eas.common.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Configuration for template subsystem.
 *
 * If application wants to use template subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class TemplateConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Constructs {@link TemplateService} bean.
     */
    @Bean
    public TemplateService templateService() {
        return new TemplateService();
    }

    /**
     * Constructs {@link TemplateCache} bean.
     */
    @Bean
    public TemplateCache templateCache() {
        return new TemplateCache();
    }
}
