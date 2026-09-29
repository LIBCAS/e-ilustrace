package cz.inqool.eas.common.exception.v2.rest.processor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.annotations.Beta;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.v2.ExceptionCode;
import cz.inqool.eas.common.exception.v2.rest.RestExceptionUtils;
import cz.inqool.eas.common.exception.v2.rest.dto.RestExceptionDto;
import cz.inqool.eas.common.exception.v2.rest.processor.MethodArgumentNotValidExceptionProcessor.ObjectErrorDto.Fields;
import cz.inqool.eas.common.utils.JsonUtils;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;

/**
 * Exception processor for {@link MethodArgumentNotValidException}
 */
@Beta
public class MethodArgumentNotValidExceptionProcessor extends RestExceptionProcessor<MethodArgumentNotValidException> {

    @Override
    protected Class<MethodArgumentNotValidException> getProcessedType() {
        return MethodArgumentNotValidException.class;
    }

    @Override
    protected ResponseEntity<RestExceptionDto> processTypedThrowable(@NonNull HttpServletRequest request, @NotNull MethodArgumentNotValidException throwable, @NotNull HttpStatus defaultHttpStatus) {
        String timestamp = RestExceptionUtils.getTimestamp();
        int status = HttpStatus.BAD_REQUEST.value();  // ignore provided defaultHttpStatus
        String urlPath = RestExceptionUtils.getUrlPath(request);
        String ipAddress = RestExceptionUtils.getClientIpAddress(request);
        UserReference user = RestExceptionUtils.getUserReference();
        String code = ExceptionCode.VALIDATION_FAILED;

        logError(throwable, urlPath, ipAddress, user);

        RestExceptionDto dto = new RestExceptionDto(timestamp, code, status, urlPath);
        dto.setMessage(throwable.getMessage());
        dto.setUser(user);
        ifPresent(throwable.getAllErrors(), objectErrors -> dto.setDetails(
                objectErrors.stream()
                        .map(ObjectErrorDto::of)
                        .collect(Collectors.toList()))
        );

        return new ResponseEntity<>(dto, HEADERS, status);
    }

    @Override
    protected String getDetailedMessage(@NonNull MethodArgumentNotValidException throwable, @NonNull String urlPath, @Nullable String ipAddress, @Nullable UserReference userReference) {
        StringBuilder msgBuilder = new StringBuilder(throwable.toString());
        msgBuilder.append("\n\t").append("endpoint = ").append(urlPath);
        if (properties.isLogIpAddress()) {
            ifPresent(ipAddress, address -> msgBuilder.append("\n\t").append("IP = ").append(address));
        }
        ifPresent(userReference, user -> msgBuilder.append("\n\t").append("user = ").append(JsonUtils.toJsonString(user)));
        ifPresent(throwable.getAllErrors(), objectErrors -> {
            List<ObjectErrorDto> objectErrorDtos = objectErrors.stream()
                    .map(ObjectErrorDto::of)
                    .collect(Collectors.toList());
            msgBuilder.append("\n\t").append("errors = ").append(JsonUtils.toJsonString(objectErrorDtos));
        });

        return msgBuilder.toString();
    }


    @Getter
    @FieldNameConstants
    @JsonInclude(NON_NULL)
    @JsonPropertyOrder({
            Fields.message,
            Fields.objectName,
            Fields.field,
            Fields.rejectedValue
    })
    public static class ObjectErrorDto {

        private final String message;
        private final String objectName;
        private String field;
        private Object rejectedValue;


        public ObjectErrorDto(FieldError error) {
            this((ObjectError) error);
            this.field = error.getField();
            this.rejectedValue = error.getRejectedValue();
        }

        public ObjectErrorDto(ObjectError error) {
            this.message = error.getDefaultMessage();
            this.objectName = error.getObjectName();
        }


        public static ObjectErrorDto of(ObjectError error) {
            if (error instanceof FieldError) {
                return new ObjectErrorDto((FieldError) error);
            } else {
                return new ObjectErrorDto(error);
            }
        }
    }
}
