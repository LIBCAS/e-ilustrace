package cz.inqool.eas.eil.notification.generator;

import cz.inqool.eas.eil.notification.template.NotificationTemplate;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for freemarker templates
 */
@Service
public class TemplateCache {

    private final Map<String, CacheItem> cache = new ConcurrentHashMap<>();

    private Configuration freemarkerConfiguration;

    /**
     * Returns cached freemarker template for given {@code notificationTemplate}. If there's no template cached, creates
     * and stores a new one using given {@code configuration} and content providen by {@code contentGetter}
     *
     * @param notificationTemplate notification template of which the freemarker template will be returned
     * @return cached template
     */
    public Template getCachedOrCompute(@NonNull NotificationTemplate notificationTemplate, @NonNull TargetProperty targetProperty) {
        String templateName = notificationTemplate.getId() + "_" + targetProperty;

        CacheItem cachedItem = cache.get(templateName);

        String templateContent;
        switch (targetProperty) {
            case SUBJECT:
                templateContent = notificationTemplate.getSubject();
                break;
            case CONTENT:
                templateContent = notificationTemplate.getContent();
                break;
            default:
                throw new IllegalArgumentException("Not supported: " + targetProperty);
        }

        if (cachedItem != null && cachedItem.equalTo(templateContent)) {
            return cachedItem.template;
        } else {
            Template template;
            try {
                template = new Template(templateName, templateContent, freemarkerConfiguration);
            } catch (IOException e) {
                throw new NotificationTemplateError("Error when creating template", e);
            }

            cache.put(templateName, new CacheItem(templateContent, template));
            return template;
        }
    }

    @Autowired
    public void setFreemarkerConfiguration(Configuration configuration) {
        this.freemarkerConfiguration = configuration;
    }

    private static class CacheItem {

        private final int contentHashCode;
        private final Template template;

        public CacheItem(@NonNull String content, @NonNull Template template) {
            this.contentHashCode = content.hashCode();
            this.template = template;
        }

        public boolean equalTo(@NonNull String content) {
            return this.contentHashCode == content.hashCode();
        }
    }

    public enum TargetProperty {
        SUBJECT,
        CONTENT
    }
}
