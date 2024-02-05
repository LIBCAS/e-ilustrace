package cz.inqool.eas.eil.notification.template;

import cz.inqool.eas.common.authored.AuthoredRepository;
import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.eil.notification.event.NotificationEvent;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationTemplateRepository extends AuthoredRepository<
        NotificationTemplate,
        NotificationTemplate,
        NotificationTemplateIndexedObject,
        AuthoredStore<NotificationTemplate, NotificationTemplate, QNotificationTemplate>,
        AuthoredIndex<NotificationTemplate, NotificationTemplate, NotificationTemplateIndexedObject>> {

    public List<NotificationTemplate> getActiveByEvent(NotificationEvent event) {
        QNotificationTemplate model = QNotificationTemplate.notificationTemplate;

        List<NotificationTemplate> templates = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.event.eq(event))
                .where(model.active.isTrue())
                .fetch();

        detachAll();

        return templates;
    }
}
