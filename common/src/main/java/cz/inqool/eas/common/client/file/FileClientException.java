package cz.inqool.eas.common.client.file;

import cz.inqool.eas.common.exception.GeneralException;

public class FileClientException extends GeneralException {

    public FileClientException(String message) {
        super(message);
    }

    public FileClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

