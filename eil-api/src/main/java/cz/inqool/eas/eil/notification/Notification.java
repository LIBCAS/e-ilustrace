package cz.inqool.eas.eil.notification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.eil.notification.event.NotificationEvent;
import cz.inqool.eas.eil.user.User;
import cz.inqool.entityviews.*;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.common.domain.DomainViews.DETAIL;
import static cz.inqool.eas.eil.notification.Notification.BASE;
import static cz.inqool.eas.eil.notification.Notification.RECEIVED_LIST;
import static cz.inqool.eas.eil.notification.Notification.RECEIVED_DETAIL;
import static cz.inqool.eas.eil.notification.NotificationType.EMAIL_VALUE;
import static cz.inqool.eas.eil.notification.NotificationType.TYPE_PROPERTY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

/**
 * Notifikace
 */
@Schema(
        oneOf = {EmailNotification.class},
        discriminatorProperty = TYPE_PROPERTY,
        discriminatorMapping = {
                @DiscriminatorMapping(value = EMAIL_VALUE, schema = EmailNotification.class),
        }
)
@ViewableClass(views = {LIST, DETAIL, BASE, RECEIVED_LIST, RECEIVED_DETAIL})
@ViewableMapping(views = {LIST, DETAIL, BASE}, mappedTo = DEFAULT)
@ViewableMapping(views = {RECEIVED_LIST, RECEIVED_DETAIL}, mappedTo = IDENTIFIED)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class, Inheritance.class}, views = {LIST, DETAIL, BASE, RECEIVED_LIST, RECEIVED_DETAIL})
@ViewableLeaf(subClasses = {
        EmailNotification.class,
})
@Getter
@Setter
@Entity
@Table(name = "eil_notification")
@Inheritance(strategy = SINGLE_TABLE)
@BatchSize(size = 100)
@JsonTypeInfo(
        use = NAME,
        include = EXISTING_PROPERTY,
        property = TYPE_PROPERTY,
        visible = true
)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = EMAIL_VALUE,       value = EmailNotification.class),
})
@FieldNameConstants
public abstract class Notification extends DatedObject<Notification> {

    public static final String RECEIVED_LIST = "received_list";
    public static final String RECEIVED_DETAIL = "received_detail";
    public static final String BASE = "base";

    /**
     * Událost
     */
    @ViewableProperty(views = {LIST, DETAIL, RECEIVED_LIST, BASE})
    @Enumerated(EnumType.STRING)
    NotificationEvent event;

    /**
     * Příjemce
     */
    @ViewableProperty(views = {LIST, DETAIL, RECEIVED_DETAIL, BASE})
    @ViewableMapping(views = {LIST, DETAIL, RECEIVED_DETAIL, BASE}, mappedTo = DETAIL)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    User recipient;

    /**
     * Předmět
     */
    @ViewableProperty(views = {LIST, DETAIL, RECEIVED_LIST, RECEIVED_DETAIL, BASE})
    String subject;

    /**
     * Obsah (tělo) notifikace
     */
    @ViewableProperty(views = {DETAIL, RECEIVED_LIST, RECEIVED_DETAIL, BASE})
    @Nationalized
    String content;

    /**
     * Typ notifikace
     */
    public abstract NotificationType getType();
}
