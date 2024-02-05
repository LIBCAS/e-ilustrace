package cz.inqool.eas.common.keyvalue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for key value subsystem.
 *
 * If application wants to use sequence subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class KeyValueConfiguration {

    /**
     * Constructs {@link KeyValueStore} bean.
     */
    @Bean
    public KeyValueStore keyValueStore() {
        return new KeyValueStore();
    }
}
