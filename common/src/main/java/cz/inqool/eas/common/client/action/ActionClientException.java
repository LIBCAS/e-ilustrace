package cz.inqool.eas.common.client.action;

import cz.inqool.eas.common.exception.GeneralException;

public class ActionClientException extends GeneralException {

    public ActionClientException(String message) {
        super(message);
    }

    public ActionClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

