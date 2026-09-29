package cz.inqool.eas.common.reporting.exception;

import cz.inqool.eas.common.exception.GeneralException;

/**
 * Exception representing error in report generator.
 *
 **/
public class ReportAggregateGeneratorException extends GeneralException {

    public ReportAggregateGeneratorException() {
    }

    public ReportAggregateGeneratorException(String message) {
        super(message);
    }

    public ReportAggregateGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportAggregateGeneratorException(Throwable cause) {
        super(cause);
    }

    public ReportAggregateGeneratorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
