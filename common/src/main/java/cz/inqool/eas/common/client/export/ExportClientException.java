package cz.inqool.eas.common.client.export;

import cz.inqool.eas.common.exception.GeneralException;

public class ExportClientException extends GeneralException {

    public ExportClientException(String message) {
        super(message);
    }

    public ExportClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

