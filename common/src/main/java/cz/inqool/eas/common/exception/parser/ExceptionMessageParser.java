package cz.inqool.eas.common.exception.parser;

/**
 * Interface for all custom exception message parsers.
 *
 * @param <T> type of exception handled by the parser
 * @deprecated replaced with {@link cz.inqool.eas.common.exception.v2.rest.processor.RestExceptionProcessor}
 */
@Deprecated
public interface ExceptionMessageParser<T extends Throwable> extends Comparable<ExceptionMessageParser<T>> {

    /**
     * Returns the type of generic class (to be able to find most suitable message parser)
     */
    Class<T> getType();

    /**
     * Returns {@code true} if this parser is able to handle exceptions of given type. On default, all subclasses of
     * parser's {@code type} can be handled.
     *
     * @param throwable exception type class
     * @return {@code true} if this parser is able to handle exceptions of given type, {@code false} otherwise
     */
    default boolean canHandle(Class<? extends Throwable> throwable) {
        return getType().isAssignableFrom(throwable);
    }

    /**
     * Returns parsed message with different format from the message provided by given {@code throwable}
     */
    String getMessage(T throwable);

    /**
     * Compares two parsers. The comparison is based on class hierarchy - the superclass is always "greater" than its
     * sub-class.
     */
    @Override
    default int compareTo(ExceptionMessageParser o) {
        Class<T> type = this.getType();
        Class<?> oType = o.getType();

        if (type.equals(oType)) {
            return 0;
        } else if (type.isAssignableFrom(oType)) {
            return 1;
        } else {
            return -1;
        }
    }
}
