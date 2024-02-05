package cz.inqool.eas.common.storage;

import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.common.storage.file.FileApi;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.storage.file.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for storage subsystem.
 *
 * If application wants to use storage subsystem,
 * it needs to extend this class and add {@link Configuration} annotation and also
 * extend {@link File} class.
 *
 */
@Slf4j
public abstract class StorageConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link FileApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for storage_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("storage_file.url", fileUrl());

        MapPropertySource propertySource = new MapPropertySource("storage_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link FileStore} bean.
     */
    @Bean
    public FileStore fileStore() {
       return new FileStore();
    }

    /**
     * Constructs {@link FileManager} bean.
     */
    @Bean
    public FileManager fileManager() {
        FileManager.FileManagerBuilder builder = FileManager.builder();

        builder.store(fileStore());
        configure(builder);

        return builder.build();
    }

    /**
     * Constructs {@link FileApi} bean.
     */
    @Bean
    public FileApi fileApi() {
        FileApi fileApi = new FileApi();
        fileApi.setManager(fileManager());

        return fileApi;
    }

    /**
     * Method for configuration the {@link FileManager}.
     *
     * @param builder Builder used for configuration
     */
    protected abstract void configure(FileManager.FileManagerBuilder builder);

    /**
     * Returns url path of {@link FileApi}.
     */
    protected abstract String fileUrl();
}
