package cz.inqool.eas.common.exception.parser;

/**
 * Default exception message parser, returning the exception message. Has the lowest priority.
 */
public class DefaultParser implements ExceptionMessageParser<Throwable> {

    @Override
    public Class<Throwable> getType() {
        return Throwable.class;
    }

    @Override
    public boolean canHandle(Class<? extends Throwable> throwable) {
        return true;
    }

    @Override
    public String getMessage(Throwable throwable) {
        return throwable.getMessage();
    }

    @Override
    public int compareTo(ExceptionMessageParser o) {
        return 1;
    }
}
