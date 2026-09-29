package cz.inqool.eas.common.exception.v2.rest.processor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.annotations.Beta;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.v2.ExceptionCode;
import cz.inqool.eas.common.exception.v2.rest.RestExceptionUtils;
import cz.inqool.eas.common.exception.v2.rest.dto.RestExceptionDto;
import cz.inqool.eas.common.exception.v2.rest.processor.ConstraintViolationExceptionProcessor.ConstraintViolationDto.Fields;
import cz.inqool.eas.common.utils.JsonUtils;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;

/**
 * Exception processor for {@link ConstraintViolationException}
 */
@Beta
public class ConstraintViolationExceptionProcessor extends RestExceptionProcessor<ConstraintViolationException> {

    @Override
    protected Class<ConstraintViolationException> getProcessedType() {
        return ConstraintViolationException.class;
    }

    @Override
    protected ResponseEntity<RestExceptionDto> processTypedThrowable(@NonNull HttpServletRequest request, @NotNull ConstraintViolationException throwable, @NotNull HttpStatus defaultHttpStatus) {
        String timestamp = RestExceptionUtils.getTimestamp();
        int status = HttpStatus.BAD_REQUEST.value(); // ignore provided 'defaultHttpStatus'
        String urlPath = RestExceptionUtils.getUrlPath(request);
        String ipAddress = RestExceptionUtils.getClientIpAddress(request);
        UserReference user = RestExceptionUtils.getUserReference();
        String code = ExceptionCode.VALIDATION_FAILED;

        logError(throwable, urlPath, ipAddress, user);

        RestExceptionDto dto = new RestExceptionDto(timestamp, code, status, urlPath);
        dto.setMessage(throwable.getMessage());
        dto.setUser(user);
        ifPresent(throwable.getConstraintViolations(), violations -> dto.setDetails(
                violations.stream()
                        .map(ConstraintViolationDto::new)
                        .collect(Collectors.toList()))
        );

        return new ResponseEntity<>(dto, HEADERS, status);
    }

    @Override
    protected String getDetailedMessage(@NonNull ConstraintViolationException throwable, @NonNull String urlPath, @Nullable String ipAddress, @Nullable UserReference userReference) {
        StringBuilder msgBuilder = new StringBuilder(throwable.toString());
        msgBuilder.append("\n\t").append("endpoint = ").append(urlPath);
        if (properties.isLogIpAddress()) {
            ifPresent(ipAddress, address -> msgBuilder.append("\n\t").append("IP = ").append(address));
        }
        ifPresent(userReference, user -> msgBuilder.append("\n\t").append("user = ").append(JsonUtils.toJsonString(user)));
        ifPresent(throwable.getConstraintViolations(), violations -> {
            List<ConstraintViolationDto> violationDtos = violations.stream()
                    .map(ConstraintViolationDto::new)
                    .collect(Collectors.toList());
            msgBuilder.append("\n\t").append("violations = ").append(JsonUtils.toJsonString(violationDtos));
        });

        return msgBuilder.toString();
    }


    @Getter
    @FieldNameConstants
    @JsonInclude(NON_NULL)
    @JsonPropertyOrder({
            Fields.propertyPath,
            Fields.invalidValue,
            Fields.message
    })
    public static class ConstraintViolationDto {

        private String propertyPath;
        private String invalidValue;
        private String message;


        public ConstraintViolationDto(ConstraintViolation<?> violation) {
            ifPresent(violation.getPropertyPath(), propertyPath -> this.propertyPath = propertyPath.toString());
            ifPresent(violation.getInvalidValue(), invalidValue -> this.invalidValue = invalidValue.toString());
            ifPresent(violation.getMessage(),      message      -> this.message      = message);
        }
    }
}
