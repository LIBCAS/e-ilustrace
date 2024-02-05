package cz.inqool.eas.common.domain.index.mapping;

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
 * Configuration for Index Mapping subsystem.
 *
 * If application wants to use index mapping subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class IndexMappingConfiguration {

    @Autowired
    private ConfigurableEnvironment env;

    /**
     * Adds custom property source to spring for setting the url path of {@link IndexMappingApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for index mapping");

        Map<String, Object> properties = new HashMap<>();
        properties.put("indexMapping.url", indexMappingUrl());

        MapPropertySource propertySource = new MapPropertySource("index_mapping_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link IndexMappingService} bean.
     */
    @Bean
    public IndexMappingService indexMappingService() {
        return new IndexMappingService();
    }

    /**
     * Constructs {@link IndexMappingApi} bean.
     */
    @Bean
    public IndexMappingApi indexMappingApi() {
        return new IndexMappingApi();
    }

    /**
     * Returns url path of {@link IndexMappingApi}.
     */
    protected abstract String indexMappingUrl();

}
