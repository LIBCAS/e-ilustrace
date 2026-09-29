package cz.inqool.eas.eil.notification;

import cz.inqool.eas.common.mail.Mail;
import cz.inqool.entityviews.ViewableAnnotation;
import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.DomainViews.DETAIL;
import static cz.inqool.eas.common.domain.DomainViews.LIST;
import static cz.inqool.eas.eil.notification.Notification.*;
import static cz.inqool.eas.eil.notification.NotificationType.EMAIL;
import static cz.inqool.eas.eil.notification.NotificationType.EMAIL_VALUE;

/**
 * Emailová notifikace
 */
@ViewableClass(views = {LIST, DETAIL, BASE, RECEIVED_LIST, RECEIVED_DETAIL})
@ViewableMapping(views = {LIST}, mappedTo = LIST)
@ViewableMapping(views = {DETAIL}, mappedTo = DETAIL)
@ViewableMapping(views = {BASE}, mappedTo = BASE)
@ViewableMapping(views = {RECEIVED_LIST}, mappedTo = RECEIVED_LIST)
@ViewableMapping(views = {RECEIVED_DETAIL}, mappedTo = RECEIVED_DETAIL)
@ViewableAnnotation(value = {Entity.class, DiscriminatorValue.class, BatchSize.class}, views = {LIST, DETAIL, BASE, RECEIVED_LIST, RECEIVED_DETAIL})
@Getter
@Setter
@Entity
@DiscriminatorValue(EMAIL_VALUE)
@BatchSize(size = 100)
@FieldNameConstants
public class EmailNotification extends Notification {
    String email;

    /**
     * Příznak, zda-li je obsah (tělo) notifikace formátované pomocí HTML
     */
    @ViewableProperty(views = {DETAIL, BASE})
    boolean html = true;

    /**
     * Zaslaný mail
     */
    @ViewableProperty(views = {DETAIL, BASE})
    @ViewableMapping(views = {DETAIL, BASE}, mappedTo = DETAIL)
    @Fetch(FetchMode.SELECT)
    @OneToOne
    @JoinColumn(name = "mail_id")
    Mail mail;


    @Override
    @NotNull
    @Schema(allowableValues = EMAIL_VALUE)
    public NotificationType getType() {
        return EMAIL;
    }
}
