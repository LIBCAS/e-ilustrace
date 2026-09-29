package cz.inqool.eas.common.exception;

/**
 * @deprecated use {@link cz.inqool.eas.common.exception.v2.EasException} instead
 */
@Deprecated
public class GeneralException extends RuntimeException {

    public GeneralException() {
    }

    public GeneralException(String message) {
        super(message);
    }

    public GeneralException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralException(Throwable cause) {
        super(cause);
    }

    public GeneralException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
