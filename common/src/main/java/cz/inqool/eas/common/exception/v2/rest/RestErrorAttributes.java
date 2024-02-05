package cz.inqool.eas.common.exception.v2.rest;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.v2.ExceptionCode;
import cz.inqool.eas.common.exception.v2.rest.dto.RestExceptionDto;
import cz.inqool.eas.common.exception.v2.rest.processor.RestExceptionProcessor;
import cz.inqool.eas.common.utils.JsonUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.RequestDispatcher;
import java.util.Map;

import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;

/**
 * Defines format of HTTP response when an error occurs within filter chain, where no {@link ExceptionHandler} is
 * considered.
 * <p>
 * Response format should be same as that of {@link RestExceptionProcessor}. Projects can override this class (or {@link
 * DefaultErrorAttributes} class) to define own format of HTTP responses when needed.
 */
@Beta
public class RestErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        String timestamp = RestExceptionUtils.getTimestamp();
        int status = RestExceptionUtils.getStatus(webRequest);
        String urlPath = RestExceptionUtils.getUrlPath(webRequest);
        UserReference user = RestExceptionUtils.getUserReference();
        String code = getCodeForStatus(status);

        RestExceptionDto dto = new RestExceptionDto(timestamp, code, status, urlPath);
        dto.setUser(user);

        ifPresent(webRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION, RequestAttributes.SCOPE_REQUEST), errorAttribute -> {
            if (errorAttribute instanceof Throwable) {
                Throwable throwable = (Throwable) errorAttribute;
                dto.setMessage(throwable.getMessage());
            }
        });
        if (dto.getMessage() == null) {
            ifPresent(webRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE, RequestAttributes.SCOPE_REQUEST), message -> {
                dto.setMessage((String) message);
            });
        }

        //noinspection unchecked
        return JsonUtils.convert(dto, Map.class, String.class, Object.class);
    }

    private String getCodeForStatus(int status) {
        switch (status) {
            case 403:
                return ExceptionCode.ENDPOINT_ACCESS_DENIED;
            case 404:
                return ExceptionCode.ENDPOINT_NOT_FOUND;
            default:
                return "NO_CODE";
        }
    }
}
