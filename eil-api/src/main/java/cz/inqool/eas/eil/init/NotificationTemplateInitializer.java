package cz.inqool.eas.eil.init;

import cz.inqool.eas.common.init.DatedInitializer;
import cz.inqool.eas.eil.notification.event.NotificationEvent;
import cz.inqool.eas.eil.notification.template.EmailNotificationTemplate;
import cz.inqool.eas.eil.notification.template.NotificationTemplate;
import cz.inqool.eas.eil.notification.template.NotificationTemplateRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ConditionalOnProperty(prefix = "eil.init.demo", name = "enabled", havingValue = "true")
@Order(10)
@Component
@Slf4j
public class NotificationTemplateInitializer extends DatedInitializer<NotificationTemplate, NotificationTemplateRepository> {
    public static final String REGISTRATION_CONFIRMATION_NOTIFICATION_TEMPLATE_ID = "f96bc09c-eabd-4ac3-b9b1-4c0883aefa74";
    public static final String PASSWORD_RESET_NOTIFICATION_TEMPLATE_ID = "f878b0eb-e25b-4979-8245-3c2181dd4317";

    @Getter
    private NotificationTemplateRepository repository;

    private ResourceLoader resourceLoader;

    @Override
    protected List<NotificationTemplate> initializeEntities() throws IOException {
        return List.of(
                newInstance(
                        REGISTRATION_CONFIRMATION_NOTIFICATION_TEMPLATE_ID,
                        NotificationEvent.CONFIRM_REGISTRATION,
                        "Potvrzení registrace",
                        "Confirm registration",
                        loadTemplate("confirm_registration.ftl"),
                        true,
                        true
                ),
                newInstance(
                        PASSWORD_RESET_NOTIFICATION_TEMPLATE_ID,
                        NotificationEvent.PASSWORD_RESET,
                        "Žádost o reset hesla",
                        "Password reset request",
                        loadTemplate("password_reset.ftl"),
                        true,
                        true
                )
        );
    }

    private NotificationTemplate newInstance(String id,
                                             NotificationEvent event,
                                             String name,
                                             String subject,
                                             String content,
                                             boolean html,
                                             boolean active) {
        EmailNotificationTemplate instance = findOrDefault(id, EmailNotificationTemplate.class);
        instance.setId(id);
        instance.setHtml(html);
        instance.setName(name);
        instance.setSubject(subject);
        instance.setContent(content);
        instance.setActive(active);
        instance.setEvent(event);

        return instance;
    }

    private String loadTemplate(String templateFileName) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Autowired
    public void setRepository(NotificationTemplateRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
