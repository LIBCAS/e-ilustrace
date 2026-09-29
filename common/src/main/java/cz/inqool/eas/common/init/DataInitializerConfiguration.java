package cz.inqool.eas.common.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for data initializer subsystem.
 *
 * If application wants to use data initializer subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class DataInitializerConfiguration {
    /**
     * Constructs {@link DelegateDataInitializer} bean.
     */
    @Bean
    public DelegateDataInitializer dataInitializer() {
        return new DelegateDataInitializer();
    }
}
