package cz.inqool.eas.eil.notification;

import cz.inqool.eas.eil.domain.LabeledEnum;
import cz.inqool.eas.eil.domain.TypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Typ notifikace
 */
@AllArgsConstructor
public enum NotificationType implements LabeledEnum<NotificationType>, TypeEnum<NotificationType, Notification> {
    /**
     * Emailová notifikace
     */
    EMAIL("Emailová notifikace") {
        @Override
        public String dtype() {
            return EMAIL_VALUE;
        }

        @Override
        public Class<? extends Notification> type() {
            return EmailNotification.class;
        }
    };

    @Getter
    private final String label;


    public static final String TYPE_PROPERTY = "type";

    /**
     * Emailová notifikace
     */
    public static final String EMAIL_VALUE = "EMAIL";
}
