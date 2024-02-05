package cz.inqool.eas.common.client.export.runner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for export runner inter service communication subsystem.
 *
 * If application wants to use this subsystem,
 * it needs to extend this class and add {@link Configuration} annotation
 * and implement the `configure` method.
 *
 */
public abstract class ExportRunnerClientConfiguration {
    @Bean
    public ExportRunnerClient exportRunnerClient() {
        ExportRunnerClient.ExportRunnerClientBuilder builder = ExportRunnerClient.builder();
        configure(builder);

        return builder.build();
    }

    /**
     * Method for configuration the {@link ExportRunnerClient}.
     *
     * @param builder Builder used for configuration
     */
    protected abstract void configure(ExportRunnerClient.ExportRunnerClientBuilder builder);
}
