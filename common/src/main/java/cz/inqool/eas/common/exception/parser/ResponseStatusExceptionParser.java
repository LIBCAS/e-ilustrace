package cz.inqool.eas.common.exception.parser;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ResponseStatusExceptionParser implements ExceptionMessageParser<ResponseStatusException> {

    @Override
    public Class<ResponseStatusException> getType() {
        return ResponseStatusException.class;
    }

    @Override
    public String getMessage(ResponseStatusException throwable) {
        return throwable.getReason();
    }
}
