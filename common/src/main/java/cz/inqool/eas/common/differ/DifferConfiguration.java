package cz.inqool.eas.common.differ;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.differ.parser.DefaultDifferPropertyParser;
import cz.inqool.eas.common.differ.rest.DifferApi;
import cz.inqool.eas.common.differ.strategy.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for differ subsystem.
 *
 * If application wants to use differ subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Beta
@Slf4j
public abstract class DifferConfiguration {

    @Autowired
    private ConfigurableEnvironment env;

    /**
     * Adds custom property source to spring for setting the url path of {@link DifferApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for differ api");

        Map<String, Object> properties = new HashMap<>();
        properties.put("differApi.url", differApiUrl());

        MapPropertySource propertySource = new MapPropertySource("differ_api_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    @Bean
    @ConditionalOnMissingBean
    public SimpleComparingStrategy defaultSimpleComparingStrategy() {
        return new SimpleComparingStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public EntityComparingStrategy defaultEntityComparingStrategy() {
        return new EntityComparingStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public EntityCollectionComparingStrategy defaultEntityCollectionComparingStrategy() {
        return new EntityCollectionComparingStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public SimpleCollectionComparingStrategy defaultSimpleCollectionComparingStrategy() {
        return new SimpleCollectionComparingStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public EmbeddedComparingStrategy defaultEmbeddedComparingStrategy() {
        return new EmbeddedComparingStrategy();
    }

    @Bean
    public DefaultDifferPropertyParser defaultDifferPropertyParser() {
        return new DefaultDifferPropertyParser();
    }

    /**
     * Constructs {@link DifferEventNotifier} bean.
     */
    @Bean
    public DifferEventNotifier differEventNotifier() {
        return new DifferEventNotifier();
    }

    /**
     * Constructs {@link DifferModule} bean.
     */
    @Bean
    public DifferModule differModule() {
        return new DifferModule();
    }

    /**
     * Constructs {@link DifferFieldsProcessor} bean.
     */
    @Bean
    public DifferFieldsProcessor fieldsProcessor() {
        return new DifferFieldsProcessor();
    }

    @Bean
    public DifferStoreEventListener storeEventListener() {
        return new DifferStoreEventListener();
    }

    /**
     * Constructs {@link DifferApi} bean.
     *
     * Override with @Bean to enable API
     */
    public DifferApi differApi() {
        return new DifferApi();
    }

    /**
     * Returns url path of {@link DifferApi}.
     */
    protected abstract String differApiUrl();

}
