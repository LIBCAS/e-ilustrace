package cz.inqool.eas.common.domain.index.reindex;

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
 * Configuration for reindex subsystem.
 *
 * If application wants to use reindex subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */

@Slf4j
public abstract class ReindexConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link ReindexApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for reindex");

        Map<String, Object> properties = new HashMap<>();
        properties.put("reindex.url", reindexUrl());

        MapPropertySource propertySource = new MapPropertySource("reindex_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    @Bean
    public ReindexService reindexService() {
        return new ReindexService();
    }

    /**
     * Constructs {@link ReindexApi} bean.
     */
    @Bean
    public ReindexApi reindexApi(ReindexService reindexService) {
        ReindexApi reindexApi = new ReindexApi();
        reindexApi.setService(reindexService);

        return reindexApi;
    }

    /**
     * Returns url path of {@link ReindexApi}.
     */
    protected abstract String reindexUrl();
}
