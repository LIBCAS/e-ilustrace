package cz.inqool.eas.eil.notification.generator;

import cz.inqool.eas.eil.notification.EmailNotification;
import cz.inqool.eas.eil.notification.event.NotificationEvent;
import cz.inqool.eas.eil.notification.template.EmailNotificationTemplate;
import cz.inqool.eas.eil.notification.template.NotificationTemplate;
import cz.inqool.eas.eil.notification.template.model.NotificationTemplateModel;
import cz.inqool.eas.eil.user.User;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;

import static cz.inqool.eas.eil.notification.generator.TemplateCache.TargetProperty.CONTENT;
import static cz.inqool.eas.eil.notification.generator.TemplateCache.TargetProperty.SUBJECT;

@Slf4j
@Service
public class NotificationGenerator {

    private TemplateCache templateCache;

    public EmailNotification createEmailNotification(@NonNull NotificationEvent event, @NonNull EmailNotificationTemplate template, @NonNull NotificationTemplateModel model, User recipient, String email) {
        EmailNotification notification = new EmailNotification();
        notification.setEmail(email);
        notification.setRecipient(recipient);
        notification.setEvent(event);
        notification.setHtml(template.isHtml());

        notification.setSubject(generate(template, SUBJECT, model));
        notification.setContent(generate(template, CONTENT, model));

        return notification;
    }

    private String generate(NotificationTemplate notificationTemplate, TemplateCache.TargetProperty targetProperty, NotificationTemplateModel model) {
        Template template = getCachedTemplate(notificationTemplate, targetProperty);

        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            log.error(String.format("Error during generation %s for template '%s' with model '%s'.", targetProperty, notificationTemplate.getId(), model.getClass()), e);
            return e.toString();
        }
    }

    private Template getCachedTemplate(NotificationTemplate notificationTemplate, @NonNull TemplateCache.TargetProperty targetProperty) {
        return templateCache.getCachedOrCompute(notificationTemplate, targetProperty);
    }

    @Autowired
    public void setTemplateCache(TemplateCache cache) {
        this.templateCache = cache;
    }
}
