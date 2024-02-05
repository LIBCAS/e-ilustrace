package cz.inqool.eas.common.action;

import cz.inqool.eas.common.exception.GeneralException;

/**
 * Exception representing problems with action execution
 **/
public class ActionException extends GeneralException {

    public ActionException() {
    }

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionException(Throwable cause) {
        super(cause);
    }

    public ActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
