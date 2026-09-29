package cz.inqool.eas.common.exception;

/**
 * The {@code DetailedException} interface should be implemented in exception classes to provide additional information
 * (as a JSON object) in error HTTP responses to the front-end for better debugging.
 */
public interface DetailedException {

    Object getDetails();
}
