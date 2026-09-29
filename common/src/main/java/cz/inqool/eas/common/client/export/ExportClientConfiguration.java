package cz.inqool.eas.common.client.export;

import cz.inqool.eas.common.client.export.ExportClient.ExportClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for export inter service communication subsystem.
 *
 * If application wants to use this subsystem,
 * it needs to extend this class and add {@link Configuration} annotation
 * and implement the `configure` method.
 *
 */
public abstract class ExportClientConfiguration {
    @Bean
    public ExportClient exportClient() {
        ExportClientBuilder builder = ExportClient.builder();
        configure(builder);

        return builder.build();
    }

    /**
     * Method for configuration the {@link ExportClient}.
     *
     * @param builder Builder used for configuration
     */
    protected abstract void configure(ExportClientBuilder builder);
}
