package cz.inqool.eas.eil.notification.template;

import cz.inqool.entityviews.ViewableAnnotation;
import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.dictionary.DictionaryViews.LABELED;
import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.common.domain.DomainViews.DETAIL;
import static cz.inqool.eas.eil.notification.NotificationType.EMAIL_VALUE;
import static cz.inqool.eas.eil.notification.template.NotificationTemplateType.EMAIL;

/**
 * Notifikační šablona pro mailové notifikace
 */
@ViewableClass(views = {CREATE, UPDATE, LIST, DETAIL, LABELED, IDENTIFIED})
@ViewableMapping(views = {CREATE}, mappedTo = CREATE)
@ViewableMapping(views = {UPDATE}, mappedTo = UPDATE)
@ViewableMapping(views = {LIST}, mappedTo = LIST)
@ViewableMapping(views = {DETAIL}, mappedTo = DETAIL)
@ViewableMapping(views = LABELED, mappedTo = LABELED)
@ViewableMapping(views = IDENTIFIED, mappedTo = IDENTIFIED)
@ViewableAnnotation(value = {Entity.class, DiscriminatorValue.class, BatchSize.class}, views = {LIST, DETAIL, LABELED, IDENTIFIED})
@Getter
@Setter
@Entity
@DiscriminatorValue(EMAIL_VALUE)
@BatchSize(size = 100)
public class EmailNotificationTemplate extends NotificationTemplate {

    /**
     * Příznak, zda-li je obsah (tělo) notifikace formátované pomocí HTML
     */
    @ViewableProperty(views = DETAIL)
    boolean html = true;


    @Override
    @NotNull
    @Schema(allowableValues = EMAIL_VALUE)
    public NotificationTemplateType getType() {
        return EMAIL;
    }
}
