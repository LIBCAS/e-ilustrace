package cz.inqool.eas.eil.notification.event;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import cz.inqool.eas.common.utils.JsonUtils;
import cz.inqool.eas.eil.domain.LabeledEnum;
import cz.inqool.eas.eil.notification.NotificationType;
import cz.inqool.eas.eil.notification.template.model.EmailModel;
import cz.inqool.eas.eil.notification.template.model.NotificationTemplateModel;
import cz.inqool.eas.eil.notification.template.model.TokenNotificationTemplateModel;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

import static cz.inqool.eas.common.utils.AssertionUtils.isTrue;
import static cz.inqool.eas.eil.notification.NotificationType.EMAIL;

@Getter
public enum NotificationEvent implements LabeledEnum<NotificationEvent> {

    CONFIRM_REGISTRATION("Potvrzení e-mailu při registraci", null, TokenNotificationTemplateModel.class, Set.of(EMAIL)),
    PASSWORD_RESET("Žádost o reset hesla", null, TokenNotificationTemplateModel.class, Set.of(EMAIL));

    private final String label;

    /**
     * Popis události
     */
    private final String description;

    /**
     * Seznam povolených typů notifikací pro tuto událost
     */
    private final Set<NotificationType> allowedTypes;

    /**
     * Typ modelu pro zástupné položky
     */
    private final Class<? extends NotificationTemplateModel> modelType;

    /**
     * JSON schema definující zástupné položky
     */
    @Getter(lazy = true)
    private final JsonSchema schema = JsonUtils.createJsonSchema(modelType);

    NotificationEvent(String label, String description, Class<? extends NotificationTemplateModel> modelType, Set<NotificationType> allowedTypes) {
        if (allowedTypes.contains(EMAIL)) {
            isTrue(EmailModel.class.isAssignableFrom(modelType), () -> new IllegalArgumentException("Model '" + modelType + "' must extend '" + EmailModel.class
                    .getSimpleName() + "' class."));
        }
        this.label = label;
        this.description = description;
        this.allowedTypes = EnumSet.copyOf(allowedTypes);
        this.modelType = modelType;
    }


    @Override
    public String getId() {
        return name();
    }
}
