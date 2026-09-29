package cz.inqool.eas.common.reporting.exception;

import cz.inqool.eas.common.exception.GeneralException;

/**
 * Exception representing missing report generator.
 *
 **/
public class ReportGeneratorMissingException extends GeneralException {

    public ReportGeneratorMissingException() {
    }

    public ReportGeneratorMissingException(String message) {
        super(message);
    }

    public ReportGeneratorMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportGeneratorMissingException(Throwable cause) {
        super(cause);
    }

    public ReportGeneratorMissingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
