package cz.inqool.eas.common.history;

import cz.inqool.eas.common.sequence.SequenceApi;
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
 * Configuration for history subsystem.
 *
 * If application wants to use history subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class HistoryConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link SequenceApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for history_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("history.url", historyUrl());

        MapPropertySource propertySource = new MapPropertySource("history_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link HistoryRepository} bean.
     */
    @Bean
    public HistoryRepository historyRepository() {
        return new HistoryRepository();
    }

    /**
     * Constructs {@link HistoryService} bean.
     */
    @Bean
    public HistoryService historyService() {
        return new HistoryService();
    }

    /**
     * Constructs {@link HistoryListener} bean.
     */
    @Bean
    public HistoryListener historyListener() {
        return new HistoryListener();
    }

    /**
     * Constructs {@link HistoryApi} bean.
     */
    @Bean
    public HistoryApi historyApi() {
        return new HistoryApi();
    }

    /**
     * Returns url path of {@link HistoryApi}.
     */
    protected abstract String historyUrl();
}
