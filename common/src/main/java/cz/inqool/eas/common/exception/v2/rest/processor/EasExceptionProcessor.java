package cz.inqool.eas.common.exception.v2.rest.processor;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.v2.EasException;
import cz.inqool.eas.common.exception.v2.LogStrategy;
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
 * Exception processor for any subclass of {@link EasException}
 */
@Beta
public class EasExceptionProcessor extends RestExceptionProcessor<EasException> {

    @Override
    protected Class<EasException> getProcessedType() {
        return EasException.class;
    }

    @Override
    protected ResponseEntity<RestExceptionDto> processTypedThrowable(@NonNull HttpServletRequest request, @NonNull EasException throwable, @NotNull HttpStatus defaultHttpStatus) {
        String timestamp = RestExceptionUtils.getTimestamp();
        int status = throwable.getHttpStatus();
        String urlPath = RestExceptionUtils.getUrlPath(request);
        String ipAddress = RestExceptionUtils.getClientIpAddress(request);
        UserReference user = RestExceptionUtils.getUserReference();
        String code = throwable.getCode();

        logError(throwable, urlPath, ipAddress, user);

        RestExceptionDto dto = new RestExceptionDto(timestamp, code, status, urlPath);
        dto.setMessage(throwable.getMessage());
        dto.setUser(user);
        dto.setDetails(throwable.getDetails());
        if (properties.isSendDebugInfo()) {
            dto.setDebugInfo(throwable.getDebugInfo());
        }

        return new ResponseEntity<>(dto, HEADERS, status);
    }

    @Override
    protected String getDetailedMessage(@NonNull EasException throwable, @NonNull String urlPath, @Nullable String ipAddress, @Nullable UserReference userReference) {
        StringBuilder msgBuilder = new StringBuilder(throwable.toString());
        msgBuilder.append("\n\tendpoint = ").append(urlPath);
        if (properties.isLogIpAddress()) {
            ifPresent(ipAddress, address -> msgBuilder.append("\n\t").append("IP = ").append(address));
        }
        ifPresent(userReference,                 user -> msgBuilder.append("\n\tuser = "      ).append(toJsonStringWithCheck(user)));
        ifPresent(throwable.getDetails(),     details -> msgBuilder.append("\n\tdetails = "   ).append(toJsonStringWithCheck(details)));
        ifPresent(throwable.getDebugInfo(), debugInfo -> msgBuilder.append("\n\tdebugInfo = " ).append(toJsonStringWithCheck(debugInfo)));

        return msgBuilder.toString();
    }

    private String toJsonStringWithCheck(Object obj) {
        try {
            return JsonUtils.toJsonString(obj);
        } catch (Exception e) {
            log.warn("Error during details serialization", e);
            return "<serialization_error>";
        }
    }

    @Override
    protected LogStrategy getLogStrategy(@NonNull EasException throwable) {
        return (throwable.getLogStrategy() != null) ? throwable.getLogStrategy() : super.getLogStrategy(throwable);
    }
}
