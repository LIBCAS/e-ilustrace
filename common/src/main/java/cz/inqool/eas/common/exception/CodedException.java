package cz.inqool.eas.common.exception;

/**
 * The {@code CodedException} interface should be implemented in exception classes to provide additional information (as
 * a predefined code) in error HTTP responses to the front-end for better debugging.
 */
public interface CodedException {

    /**
     * Returns error code as an enum value.
     */
    Enum<? extends ExceptionCodeEnum<?>> getErrorCode();

    /**
     * Class used to mark an enum class as a source for exception codes.
     */
    interface ExceptionCodeEnum<E extends Enum<E>> {
    }
}
