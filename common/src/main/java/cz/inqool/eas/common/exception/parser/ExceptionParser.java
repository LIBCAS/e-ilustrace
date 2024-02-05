package cz.inqool.eas.common.exception.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Set;

/**
 * Component class for custom error message extraction (to provide more user-friendly description)
 */
@Component
@ApplicationScope
public class ExceptionParser {

    private static final ExceptionMessageParser<Throwable> DEFAULT_PARSER = new DefaultParser();

    private Set<ExceptionMessageParser<? extends Throwable>> parsers;


    public String getMessage(Throwable throwable) {
        ExceptionMessageParser parser = parsers.stream()
                .filter(o -> o.canHandle(throwable.getClass()))
                .sorted()
                .findFirst().orElse(DEFAULT_PARSER);

        return parser.getMessage(throwable);
    }


    @Autowired
    public void setExceptionParsers(Set<ExceptionMessageParser<? extends Throwable>> parsers) {
        this.parsers = parsers;
    }
}
