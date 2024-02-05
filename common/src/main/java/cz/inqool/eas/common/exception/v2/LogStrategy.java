package cz.inqool.eas.common.exception.v2;

/**
 * Strategy used for exception logging.
 */
public enum LogStrategy {

    /**
     * Log all exception details ("toString" + details + stacktrace)
     */
    ALL,

    /**
     * Log only brief exception information (using "toString" method)
     */
    BRIEF,

    /**
     * Log brief exception information (using "toString" method) with stacktrace
     */
    BRIEF_WITH_STACKTRACE,

    /**
     * Log fully described exception without stacktrace ("toString" + details)
     */
    DETAILED,

    /**
     * Create no log entry
     */
    NONE
}
