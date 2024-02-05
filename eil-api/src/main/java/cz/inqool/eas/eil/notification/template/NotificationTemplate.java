package cz.inqool.eas.eil.notification.template;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.domain.index.reference.Labeled;
import cz.inqool.eas.eil.notification.event.NotificationEvent;
import cz.inqool.entityviews.*;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static cz.inqool.eas.common.dictionary.DictionaryViews.LABELED;
import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.common.domain.DomainViews.LIST;
import static cz.inqool.eas.eil.notification.template.NotificationTemplateType.EMAIL_VALUE;
import static cz.inqool.eas.eil.notification.template.NotificationTemplateType.TYPE_PROPERTY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

/**
 * Notifikační šablona
 */
@Schema(
        oneOf = {EmailNotificationTemplate.class},
        discriminatorProperty = TYPE_PROPERTY,
        discriminatorMapping = {
                @DiscriminatorMapping(value = EMAIL_VALUE, schema = EmailNotificationTemplate.class),
        }
)
@DomainViews
@ViewableClass(views = {LABELED, IDENTIFIED})
@ViewableMapping(views = {LABELED, IDENTIFIED}, mappedTo = IDENTIFIED)
@ViewableImplement(value = Labeled.class, views = {DETAIL, LIST, LABELED})
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class, Inheritance.class}, views = {LABELED, IDENTIFIED})
@ViewableAnnotation(value = {FieldNameConstants.class}, views = "none")
@ViewableLeaf(subClasses = {
        EmailNotificationTemplate.class,
})
@Getter
@Setter
@Entity
@Table(name = "eil_notification_template")
@Inheritance(strategy = SINGLE_TABLE)
@BatchSize(size = 100)
@JsonTypeInfo(
        use = NAME,
        include = EXISTING_PROPERTY,
        property = TYPE_PROPERTY,
        visible = true
)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = EMAIL_VALUE,       value = EmailNotificationTemplate.class),
})
@FieldNameConstants
public abstract class NotificationTemplate extends AuthoredObject<NotificationTemplate> implements Labeled {

    /**
     * Verze záznamu
     */
    @Schema(description = "Verze záznamu")
    @ViewableProperty(views = {DETAIL, LIST, UPDATE})
    @Version
    protected int version;

    /**
     * Název šablony
     */
    @NotBlank
    @ViewableProperty(views = {LIST, DETAIL, CREATE, UPDATE, LABELED, INDEXED})
    protected String name;

    /**
     * Událost
     */
    @NotNull
    @ViewableProperty(views = {LIST, DETAIL, CREATE, UPDATE, INDEXED})
    @Enumerated(EnumType.STRING)
    protected NotificationEvent event;

    /**
     * Předmět notifikace
     */
    @NotBlank
    @ViewableProperty(views = {LIST, DETAIL, CREATE, UPDATE, INDEXED})
    @Nationalized
    protected String subject;

    /**
     * Obsah (tělo) notifikace
     */
    @NotBlank
    @ViewableProperty(views = {DETAIL, CREATE, UPDATE})
    @Nationalized
    protected String content;

    /**
     * Příznak platnosti
     */
    @ViewableProperty(views = {LIST, DETAIL, INDEXED})
    protected boolean active = true;

    /**
     * Typ notifikační šablony
     */
    public abstract NotificationTemplateType getType();

    @Override
    @ViewableProperty(views = {DETAIL, LIST, LABELED})
    public String getLabel() {
        return name;
    }
}
