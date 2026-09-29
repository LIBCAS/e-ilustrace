package cz.inqool.eas.common.exception;

import java.util.function.Function;

public class ExceptionUtils {

    /**
     * Return last cause in causual chain of given throwable
     */
    public static Throwable getLastCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause;
    }

    /**
     * Execute given {@code operation}. When a {@link GeneralException} is thrown during execution, it is thrown further
     * untouched. Otherwise, if an exception of different class is thrown during execution, it is wrapped in {@link
     * GeneralException}.
     *
     * @param operation operation to be executed (may throw an exception of type {@code T})
     * @param <T>       the type of exception that may be thrown during execution
     */
    public static <T extends Throwable> void checked(CheckedOperation<T> operation) {
        checked(operation, exceptionThrown -> {
            if (exceptionThrown instanceof GeneralException) {
                return (GeneralException) exceptionThrown;
            } else {
                return new GeneralException(exceptionThrown);
            }
        });
    }

    /**
     * Execute given {@code operation}. When an exception is thrown during execution, the given {@code supplier} is used
     * to raise custom exception.
     *
     * @param operation operation to be executed (may throw an exception of type {@code T})
     * @param supplier  fuction to provide custom exception to be raised
     * @param <T>       the type of exception that may be thrown during execution
     * @param <E>       the type of exception to be providen by supplier to be raised
     */
    public static <T extends Throwable, E extends RuntimeException> void checked(CheckedOperation<T> operation, Function<T, E> supplier) {
        try {
            operation.execute();
        } catch (Throwable ex) {
            throw supplier.apply((T) ex);
        }
    }

    /**
     * Execute given {@code operation}. When a {@link GeneralException} is thrown during execution, it is thrown further
     * untouched. Otherwise, if an exception of different class is thrown during execution, it is wrapped in {@link
     * GeneralException}.
     *
     * @param operation operation to be executed (may throw an exception of type {@code T})
     * @param <T>       the type of exception that may be thrown during execution
     */
    public static <T extends Throwable, R> R checked(CheckedOperationWithReturn<T, R> operation) {
        return checked(operation, exceptionThrown -> {
            if (exceptionThrown instanceof GeneralException) {
                return (GeneralException) exceptionThrown;
            } else {
                return new GeneralException(exceptionThrown);
            }
        });
    }

    /**
     * Execute given {@code operation}. When an exception is thrown during execution, the given {@code supplier} is used
     * to raise custom exception.
     *
     * @param operation operation to be executed (may throw an exception of type {@code T})
     * @param supplier  fuction to provide custom exception to be raised
     * @param <T>       the type of exception that may be thrown during execution
     * @param <E>       the type of exception to be providen by supplier to be raised
     */
    public static <T extends Throwable, R, E extends RuntimeException> R checked(CheckedOperationWithReturn<T, R> operation, Function<T, E> supplier) {
        try {
            return operation.execute();
        } catch (Throwable ex) {
            throw supplier.apply((T) ex);
        }
    }

    /**
     * Represents an operation that returns no result. The function may throw an exception of defined type. Unlike most
     * other functional interfaces, {@code CheckedOperation} is expected to operate via side-effects.
     *
     * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is {@link
     * #execute()}.
     *
     * @param <EX> the type of the exception that may be thrown
     */
    @FunctionalInterface
    public interface CheckedOperation<EX extends Throwable> {

        void execute() throws EX;
    }

    @FunctionalInterface
    public interface CheckedOperationWithReturn<EX extends Throwable, R> {

        R execute() throws EX;
    }
}
