package cz.inqool.eas.eil.notification.generator;

import cz.inqool.eas.common.exception.v2.EasException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * Class for throwing exceptions when there is problem with method's argument
 */
@Getter
public class NotificationTemplateError extends EasException {

    public NotificationTemplateError(@NonNull String code, Throwable cause) {
        this(code, HttpStatus.INTERNAL_SERVER_ERROR.value(), null, cause);
    }

    public NotificationTemplateError(@NonNull String code, int httpStatus, String message, Throwable cause) {
        super(code, httpStatus, message, cause);
    }
}
