package cz.inqool.eas.common.exception.parser;

import cz.inqool.eas.common.exception.GeneralException;
import org.springframework.stereotype.Component;

@Component
public class GeneralExceptionParser implements ExceptionMessageParser<GeneralException> {

    @Override
    public Class<GeneralException> getType() {
        return GeneralException.class;
    }

    @Override
    public String getMessage(GeneralException throwable) {
        return throwable.toString();
    }
}
