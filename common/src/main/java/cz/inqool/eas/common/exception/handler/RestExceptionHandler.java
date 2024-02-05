package cz.inqool.eas.common.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.exception.*;
import cz.inqool.eas.common.exception.dto.ObfuscatedException;
import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.common.exception.parser.ExceptionParser;
import cz.inqool.eas.common.exception.v2.EasException;
import cz.inqool.eas.common.security.captcha.exception.ReCaptchaInvalidException;
import cz.inqool.eas.common.security.form.twoFactor.TwoFactorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.search.SearchParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

import static cz.inqool.eas.common.exception.handler.RestExceptionHandler.Statics.HEADERS;
import static cz.inqool.eas.common.exception.handler.RestExceptionHandler.Statics.IGNORED_EXCEPTIONS;

/**
 * Class handling exceptions raised during REST endpoint executions. Provides unified and more detailed information
 * about exceptions.
 *
 * @deprecated deprecated in favor of {@link cz.inqool.eas.common.exception.v2.rest.RestExceptionHandler}
 */
@Slf4j
@ControllerAdvice
@ConditionalOnProperty(prefix = "eas.exception.handler.rest", name = "legacy", havingValue = "true", matchIfMissing = true)
@Deprecated
public class RestExceptionHandler {
    private ExceptionParser exceptionParser;

    private EventService eventService;

    private EventBuilder eventBuilder;


    @PostConstruct
    public void init() {
        log.warn("Old REST exception handler is still used. Consider to update your project to use new / enhanced version");
    }


    @ExceptionHandler({
            EasException.class
    })
    public ResponseEntity<RestException> easException(HttpServletRequest request, EasException e) {
        HttpStatus httpStatus;
        try {
            httpStatus = HttpStatus.valueOf(e.getHttpStatus());
        } catch (IllegalArgumentException ignored) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        RestException error = new RestException(request, httpStatus, e, exceptionParser.getMessage(e));
        return defaultExceptionHandling(error ,request, e, httpStatus, true);
    }

    @ExceptionHandler({
            BadArgument.class,
            BindException.class,
            HttpMediaTypeException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            InvalidAttribute.class,
            InvalidArgument.class,
            MissingAttribute.class,
            MethodArgumentNotValidException.class,
            JsonProcessingException.class,
            ConstraintViolationException.class,
            VirusFoundException.class,
            ReCaptchaInvalidException.class
    })
    public ResponseEntity<RestException> badRequest(HttpServletRequest request, Exception e) {
        return defaultExceptionHandling(request, e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            HttpMessageConversionException.class
    })
    public ResponseEntity<RestException> badConversion(HttpServletRequest request, Exception e) {
        if (e.getCause() instanceof JsonMappingException) {
            JsonMappingException cause = (JsonMappingException) e.getCause();
            if (cause.getCause() instanceof GeneralException) {
                return badRequest(request, (GeneralException) cause.getCause());
            }
        }
        return defaultExceptionHandling(request, e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            TwoFactorException.class
    })
    public ResponseEntity<RestException> unauthorized(HttpServletRequest request, Exception e) {
        return defaultExceptionHandling(request, e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            ForbiddenObject.class,
            ForbiddenOperation.class,
            AccessDeniedException.class,
            ExtensionNotAllowedException.class
    })
    public ResponseEntity<RestException> forbidden(HttpServletRequest request, Exception e) {
        if (eventService != null) {
            eventService.create(eventBuilder.forbiddenUrl(request.getRequestURI(), UserGenerator.generateValue()));
        }

        return defaultExceptionHandling(request, e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            MissingObject.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<RestException> notFound(HttpServletRequest request, Exception e) {
        return defaultExceptionHandling(request, e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            ConflictObject.class
    })
    public ResponseEntity<RestException> conflict(HttpServletRequest request, Exception e) {
        return defaultExceptionHandling(request, e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            SearchPhaseExecutionException.class
    })
    public ResponseEntity<RestException> preconditionFailed(HttpServletRequest request, Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e.getCause() != null && e.getCause() instanceof SearchParseException && e.getCause().getMessage() != null && e.getCause().getMessage().startsWith("No mapping found for")) {
            status = HttpStatus.PRECONDITION_FAILED;
        }
        return defaultExceptionHandling(request, e, status);
    }

    @ExceptionHandler({
            ResponseStatusException.class
    })
    public ResponseEntity<RestException> responseStatusException(HttpServletRequest request, Exception e) {
        ResponseStatusException responseStatusException = (ResponseStatusException) e;
        return defaultExceptionHandling(request, e, responseStatusException.getStatus());
    }

    /**
     * Do not log ClientAbortException as it means, the client application closes the connection before
     * server send the response, which is not a fault of the server.
     */
    @ExceptionHandler({
            ClientAbortException.class
    })
    public void clientAbortException(HttpServletRequest request, Exception e) {
    }

    @ExceptionHandler({
            Exception.class
    })
    public ResponseEntity<RestException> exception(HttpServletRequest request, Exception e) {
        return defaultExceptionHandling(request, e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<RestException> defaultExceptionHandling(HttpServletRequest request, Exception e, HttpStatus status) {
        RestException error = new RestException(request, status, e, exceptionParser.getMessage(e));
        return defaultExceptionHandling(error ,request, e, status, true);
    }

    private ResponseEntity<RestException> defaultExceptionHandling(RestException error, HttpServletRequest request, Exception e, HttpStatus status, boolean logError) {
        if (logError) {
            String msg = "\n" + error.toString();
            if (IGNORED_EXCEPTIONS.contains(error.toObfuscatedException())) { // do not log stacktrace of ignored exceptions
                log.error(msg);
            } else {
                log.error(msg, e);
            }
        }
        return new ResponseEntity<>(error, HEADERS, status);
    }


    @Autowired
    public void setExceptionParser(ExceptionParser parser) {
        this.exceptionParser = parser;
    }

    @Autowired(required = false)
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @Autowired(required = false)
    public void setEventBuilder(EventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
    }

    /**
     * Class containing static configuration variables
     */
    static class Statics {

        /** Set of exceptions which will be logged only limitedly */
        static final Set<ObfuscatedException> IGNORED_EXCEPTIONS = new HashSet<>();

        /** HTTP Headers which will be included on all REST error responses */
        static final HttpHeaders HEADERS = new HttpHeaders();

        static {
            IGNORED_EXCEPTIONS.addAll(Set.of(
                    new ObfuscatedException(MethodArgumentNotValidException.class, null, null),
                    new ObfuscatedException(AccessDeniedException.class, null, null)
//                    new IgnoredException(SearchPhaseExecutionException.class, "all shards failed", null)
            ));

            HEADERS.setContentType(MediaType.APPLICATION_JSON);
        }
    }


}
