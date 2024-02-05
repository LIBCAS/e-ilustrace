package cz.inqool.eas.common.i18n.yml;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;

/**
 * Reloadable resource bundle message source providing support for YAML-defined message sources.
 */
public class YamlReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

    private static final String YML_SUFFIX = ".yml";
    private final ConcurrentMap<String, PropertiesHolder> cachedProperties;
    private ResourceLoader resourceLoader;


    public YamlReloadableResourceBundleMessageSource(MessageSourceProperties properties) {
        super();

        setBasename(properties.getBasename());
        setDefaultEncoding(properties.getEncoding().name());
        setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        ifPresent(properties.getCacheDuration(), duration -> setCacheMillis(duration.toMillis()));

        Field resourceLoaderField = ReflectionUtils.findField(ReloadableResourceBundleMessageSource.class, "resourceLoader");
        ReflectionUtils.makeAccessible(resourceLoaderField);
        this.resourceLoader = (ResourceLoader) ReflectionUtils.getField(resourceLoaderField, this);

        Field cachedPropertiesField = ReflectionUtils.findField(ReloadableResourceBundleMessageSource.class, "cachedProperties");
        ReflectionUtils.makeAccessible(cachedPropertiesField);
        //noinspection unchecked
        this.cachedProperties = (ConcurrentMap<String, PropertiesHolder>) ReflectionUtils.getField(cachedPropertiesField, this);
    }


    /**
     * Overrides the parent method to support yml-defined resource bundles
     */
    @Override
    protected PropertiesHolder refreshProperties(@NotNull String filename, @Nullable PropertiesHolder propHolder) {
        long refreshTimestamp = (getCacheMillis() < 0 ? -1 : System.currentTimeMillis());

        Resource resource = this.resourceLoader.getResource(filename + YML_SUFFIX);

        if (resource.exists()) {
            long fileTimestamp = -1;
            if (getCacheMillis() >= 0) {
                // Last-modified timestamp of file will just be read if caching with timeout.
                try {
                    fileTimestamp = resource.lastModified();
                    if (propHolder != null && propHolder.getFileTimestamp() == fileTimestamp) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Re-caching properties for filename [" + filename + "] - file hasn't been modified");
                        }
                        propHolder.setRefreshTimestamp(refreshTimestamp);
                        return propHolder;
                    }
                } catch (IOException ex) {
                    // Probably a class path resource: cache it forever.
                    if (logger.isDebugEnabled()) {
                        logger.debug(resource + " could not be resolved in the file system - assuming that it hasn't changed", ex);
                    }
                    fileTimestamp = -1;
                }
            }
            Properties props = loadProperties(resource, filename);
            propHolder = new PropertiesHolder(props, fileTimestamp);
        } else {
            // Resource does not exist.
            if (logger.isDebugEnabled()) {
                logger.debug("No properties file found for [" + filename + "] - neither plain properties nor XML");
            }
            // Empty holder representing "not found".
            propHolder = new PropertiesHolder();
        }

        propHolder.setRefreshTimestamp(refreshTimestamp);
        this.cachedProperties.put(filename, propHolder);
        return propHolder;
    }


    /**
     * Overrides the parent method to support yml-defined resource bundles
     */
    @Override
    protected Properties loadProperties(@NotNull Resource resource, @NotNull String filename) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading properties [" + resource.getFilename() + "]");
        }
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(resource);
        return bean.getObject();
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        super.setResourceLoader(resourceLoader);
        this.resourceLoader = resourceLoader;
    }
}
