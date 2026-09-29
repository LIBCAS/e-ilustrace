package cz.inqool.eas.common.exception.parser;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class MethodArgumentNotValidExceptionParser implements ExceptionMessageParser<MethodArgumentNotValidException> {

    @Override
    public Class<MethodArgumentNotValidException> getType() {
        return MethodArgumentNotValidException.class;
    }

    @Override
    public String getMessage(MethodArgumentNotValidException throwable) {
        StringBuilder sb = new StringBuilder();
        BindingResult bindingResult = throwable.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        if (fieldError != null) {
            sb/*.append(fieldError.getObjectName()).append(".")*/
                    .append("@").append(fieldError.getCode()).append(": ")
                    .append(fieldError.getField()).append("->{")
                    .append(fieldError.getRejectedValue()).append("} (")
                    .append(fieldError.getDefaultMessage()).append(")");
        } else {
            sb.append("Validation failed without fieldError");
        }
        return sb.toString();
    }
}
