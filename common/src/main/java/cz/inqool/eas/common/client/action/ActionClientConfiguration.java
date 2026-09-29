package cz.inqool.eas.common.client.action;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for action inter service communication subsystem.
 *
 * If application wants to use this subsystem,
 * it needs to extend this class and add {@link Configuration} annotation
 * and implement the `configure` method.
 *
 */
public abstract class ActionClientConfiguration {
    @Bean
    public ActionClient actionClient() {
        ActionClient.ActionClientBuilder builder = ActionClient.builder();
        configure(builder);

        return builder.build();
    }

    /**
     * Method for configuration the {@link ActionClient}.
     *
     * @param builder Builder used for configuration
     */
    protected abstract void configure(ActionClient.ActionClientBuilder builder);
}
