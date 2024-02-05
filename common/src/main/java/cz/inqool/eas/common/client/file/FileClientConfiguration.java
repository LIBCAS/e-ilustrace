package cz.inqool.eas.common.client.file;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for file inter service communication subsystem.
 *
 * If application wants to use this subsystem,
 * it needs to extend this class and add {@link Configuration} annotation
 * and implement the `configure` method.
 *
 */
public abstract class FileClientConfiguration {
    @Bean
    public FileClient fileClient() {
        FileClient.FileClientBuilder builder = FileClient.builder();
        configure(builder);

        return builder.build();
    }

    /**
     * Method for configuration the {@link FileClient}.
     *
     * @param builder Builder used for configuration
     */
    protected abstract void configure(FileClient.FileClientBuilder builder);
}
