package cz.inqool.eas.eil.notification.template;

import cz.inqool.eas.eil.domain.LabeledEnum;
import cz.inqool.eas.eil.domain.TypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Typ notifikační šablony
 */
@AllArgsConstructor
public enum NotificationTemplateType implements LabeledEnum<NotificationTemplateType>, TypeEnum<NotificationTemplateType, NotificationTemplate> {

    /**
     * Notifikační šablona pro mailové notifikace
     */
    EMAIL("Notifikační šablona pro mailové notifikace") {
        @Override
        public String dtype() {
            return EMAIL_VALUE;
        }

        @Override
        public Class<? extends NotificationTemplate> type() {
            return EmailNotificationTemplate.class;
        }
    };


    @Getter
    private final String label;

    static final String TYPE_PROPERTY = "type";

    /**
     * Notifikační šablona pro mailové notifikace
     */
    public static final String EMAIL_VALUE = "EMAIL";
}
