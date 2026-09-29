package cz.inqool.eas.common.exception.parser;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Component
public class ConstraintViolationExceptionParser implements ExceptionMessageParser<ConstraintViolationException> {

    @Override
    public Class<ConstraintViolationException> getType() {
        return ConstraintViolationException.class;
    }

    @Override
    public String getMessage(ConstraintViolationException throwable) {
        StringBuilder sb = new StringBuilder(throwable.getMessage());

        if (!throwable.getConstraintViolations().isEmpty()) {
            for (ConstraintViolation<?> violation : throwable.getConstraintViolations()) {
                sb.append(" ").append(violation.toString());
            }
        }
        return sb.toString();
    }
}
