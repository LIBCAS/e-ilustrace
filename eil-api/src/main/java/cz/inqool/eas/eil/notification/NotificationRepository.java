package cz.inqool.eas.eil.notification;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepository extends DatedRepository<
        Notification,
        Notification,
        NotificationIndexedObject,
        DatedStore<Notification, Notification, QNotification>,
        DatedIndex<Notification, Notification, NotificationIndexedObject>> {
}

