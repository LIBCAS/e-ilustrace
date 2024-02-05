package cz.inqool.eas.common.exception.v2.rest.processor;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.CodedException;
import cz.inqool.eas.common.exception.DetailedException;
import cz.inqool.eas.common.exception.GeneralException;
import cz.inqool.eas.common.exception.v2.rest.RestExceptionUtils;
import cz.inqool.eas.common.exception.v2.rest.dto.RestExceptionDto;
import cz.inqool.eas.common.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;

/**
 * Legacy exception processor for any {@link GeneralException} subclass.
 */
@Beta
public class GeneralExceptionProcessor extends RestExceptionProcessor<GeneralException> {

    @Override
    protected Class<GeneralException> getProcessedType() {
        return GeneralException.class;
    }

    @Override
    protected ResponseEntity<RestExceptionDto> processTypedThrowable(@NonNull HttpServletRequest request, @NotNull GeneralException throwable, @NotNull HttpStatus defaultHttpStatus) {
        String timestamp = RestExceptionUtils.getTimestamp();
        int status = defaultHttpStatus.value();
        String urlPath = RestExceptionUtils.getUrlPath(request);
        String ipAddress = RestExceptionUtils.getClientIpAddress(request);
        UserReference user = RestExceptionUtils.getUserReference();
        String code = getErrorCode(throwable);

        logError(throwable, urlPath, ipAddress, user);

        RestExceptionDto dto = new RestExceptionDto(timestamp, code, status, urlPath);
//        dto.setMessage(e.getMessage()); // ignore message as in the end it is simple 'toString' method call
        dto.setUser(user);
        if (properties.isSendDebugInfo()) {
            dto.setDebugInfo(getDetails(throwable));
        }

        return new ResponseEntity<>(dto, HEADERS, status);
    }

    @Override
    protected String getDetailedMessage(@NonNull GeneralException throwable, @NonNull String urlPath, @Nullable String ipAddress, @Nullable UserReference userReference) {
        StringBuilder msgBuilder = new StringBuilder(throwable.toString());
        msgBuilder.append("\n\t").append("endpoint = ").append(urlPath);
        if (properties.isLogIpAddress()) {
            ifPresent(ipAddress, address -> msgBuilder.append("\n\t").append("IP = ").append(address));
        }
        ifPresent(userReference, user -> msgBuilder.append("\n\t").append("user = ").append(JsonUtils.toJsonString(user)));
        if (throwable instanceof DetailedException) {
            ifPresent(((DetailedException) throwable).getDetails(), details -> msgBuilder.append("\n\t").append("details = ").append(JsonUtils.toJsonString(details)));
        }

        return msgBuilder.toString();
    }

    private String getErrorCode(GeneralException throwable) {
        if (throwable instanceof CodedException) {
            Enum<? extends CodedException.ExceptionCodeEnum<?>> errorCode = ((CodedException) throwable).getErrorCode();
            if (errorCode != null) {
                return errorCode.name();
            }
        }

        return "NO_CODE";
    }

    private Object getDetails(GeneralException throwable) {
        if (throwable instanceof DetailedException) {
            return ((DetailedException) throwable).getDetails();
        }

        return null;
    }
}
