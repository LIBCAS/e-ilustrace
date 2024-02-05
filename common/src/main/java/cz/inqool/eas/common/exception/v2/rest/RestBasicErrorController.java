package cz.inqool.eas.common.exception.v2.rest;

import cz.inqool.eas.common.exception.v2.ExceptionConfigurationProperties.HandlerProperties.RestProperties;
import cz.inqool.eas.common.exception.v2.rest.dto.RestExceptionDto;
import cz.inqool.eas.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;
import static cz.inqool.eas.common.utils.JsonUtils.toJsonString;

/**
 * Basic error controler for REST endpoint, adds logging to error handling
 */
@Slf4j
public class RestBasicErrorController extends BasicErrorController {

    private final RestProperties restProperties;


    public RestBasicErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties, RestProperties restProperties) {
        super(errorAttributes, errorProperties);
        this.restProperties = restProperties;
    }

    public RestBasicErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties, RestProperties restProperties, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorProperties, errorViewResolvers);
        this.restProperties = restProperties;
    }


    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        return super.errorHtml(request, response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        ResponseEntity<Map<String, Object>> response = super.error(request);

        RestExceptionDto restExceptionDto = JsonUtils.convert(response.getBody(), RestExceptionDto.class);
        String clientIpAddress = RestExceptionUtils.getClientIpAddress(request);
        log(restExceptionDto, clientIpAddress);

        return response;
    }

    public void log(RestExceptionDto dto, String clientIpAddress) {
        StringBuilder msgBuilder = new StringBuilder();
        ifPresent(dto.getMessage(), msgBuilder::append);
        ifPresent(dto.getStatus(), status  -> msgBuilder.append("\n\t").append("status = "    ).append(status));
        ifPresent(dto.getPath(),   path    -> msgBuilder.append("\n\t").append("endpoint = "  ).append(path));
        if (restProperties.isLogIpAddress()) {
            ifPresent(clientIpAddress, address -> msgBuilder.append("\n\t").append("IP = ").append(address));
        }
        ifPresent(dto.getCode(),   code    -> msgBuilder.append("\n\t").append("code = "      ).append(code));
        ifPresent(dto.getUser(),   user    -> msgBuilder.append("\n\t").append("user = "      ).append(toJsonString(user)));

        log.error(msgBuilder.toString());
    }
}
