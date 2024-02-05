package cz.inqool.eas.common.exception.v2.rest.processor;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.v2.rest.RestExceptionUtils;
import cz.inqool.eas.common.exception.v2.rest.dto.RestExceptionDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;

/**
 * Default processor used when no other more suitable processor is found. Should be always present to be able to handle
 * all exceptions thrown in the project.
 */
@Beta
public class DefaultExceptionProcessor extends RestExceptionProcessor<Throwable> {

    @Override
    public int getSuitabilityFor(@NonNull Class<? extends Throwable> throwableClass) {
        return Integer.MAX_VALUE;
    }

    @Override
    protected Class<Throwable> getProcessedType() {
        return Throwable.class;
    }

    @Override
    protected ResponseEntity<RestExceptionDto> processTypedThrowable(@NonNull HttpServletRequest request, @NotNull Throwable throwable, @NotNull HttpStatus defaultHttpStatus) {
        String timestamp = RestExceptionUtils.getTimestamp();
        int status = defaultHttpStatus.value();
        String urlPath = RestExceptionUtils.getUrlPath(request);
        String ipAddress = RestExceptionUtils.getClientIpAddress(request);
        UserReference user = RestExceptionUtils.getUserReference();
        String code = "NO_CODE";

        logError(throwable, urlPath, ipAddress, user);

        RestExceptionDto dto = new RestExceptionDto(timestamp, code, status, urlPath);
        dto.setMessage(throwable.getMessage());
        dto.setUser(user);

        return new ResponseEntity<>(dto, HEADERS, status);
    }
}
