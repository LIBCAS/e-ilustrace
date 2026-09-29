package cz.inqool.eas.common.admin.console;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Configuration for admin console subsystem.
 *
 * If application wants to use admin console subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 * You will want {@link cz.inqool.eas.common.stomp.StompConfiguration} too.
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public abstract class AdminConsoleConfiguration {
    /**
     * Constructs {@link AdminConsoleService} bean.
     */
    @Bean
    public AdminConsoleService adminConsoleService() {
        return new AdminConsoleService();
    }

    /**
     * Constructs {@link AdminConsoleOutputRouter} bean.
     */
    @Bean
    public AdminConsoleOutputRouter adminConsoleOutputRouter() {
        return new AdminConsoleOutputRouter();
    }
}
