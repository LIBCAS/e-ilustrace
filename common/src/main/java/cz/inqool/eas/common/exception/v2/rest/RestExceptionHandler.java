package cz.inqool.eas.common.exception.v2.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.annotations.Beta;
import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.exception.*;
import cz.inqool.eas.common.exception.v2.EasException;
import cz.inqool.eas.common.exception.v2.ExtensionNotAllowedException;
import cz.inqool.eas.common.exception.v2.rest.dto.RestExceptionDto;
import cz.inqool.eas.common.exception.v2.rest.processor.RestExceptionProcessor;
import cz.inqool.eas.common.security.captcha.exception.ReCaptchaInvalidException;
import cz.inqool.eas.common.security.form.twoFactor.TwoFactorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class handling exceptions raised during REST endpoint executions. Provides unified and more detailed information
 * about exceptions.
 */
@Beta
@Slf4j
@Component("RestExceptionHandlerV2")
@RestControllerAdvice
@ConditionalOnProperty(prefix = "eas.exception.handler.rest", name = "legacy", havingValue = "false")
public class RestExceptionHandler {

    private final Map<Class<? extends Throwable>, RestExceptionProcessor<?>> processorMap = new HashMap<>();

    private EventService eventService; // todo what about this? maybe move to processors
    private EventBuilder eventBuilder; // todo what about this? maybe move to processors
    private List<RestExceptionProcessor<?>> processors;


    @ExceptionHandler({
            EasException.class,
            Throwable.class
    })
    public ResponseEntity<RestExceptionDto> internalServerError(HttpServletRequest request, Throwable e) {
        return handleThrowable(request, e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            BadArgument.class,
            BindException.class,
            ConstraintViolationException.class,
            HttpMediaTypeException.class,
            HttpMessageNotReadableException.class,
            InvalidArgument.class,
            InvalidAttribute.class,
            JsonProcessingException.class,
            MethodArgumentNotValidException.class,
            MissingAttribute.class,
            MissingServletRequestParameterException.class,
            ReCaptchaInvalidException.class,
            VirusFoundException.class
    })
    public ResponseEntity<RestExceptionDto> badRequest(HttpServletRequest request, Throwable e) {
        return handleThrowable(request, e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            HttpMessageConversionException.class
    })
    public ResponseEntity<RestExceptionDto> badConversion(HttpServletRequest request, Throwable e) {
        Throwable specificException = e;
        if (e.getCause() instanceof JsonMappingException) {
            JsonMappingException jsonMappingExceptionCause = (JsonMappingException) e.getCause();
            if (jsonMappingExceptionCause.getCause() instanceof GeneralException) {
                specificException = jsonMappingExceptionCause.getCause();
            }
        }
        return handleThrowable(request, specificException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            TwoFactorException.class
    })
    public ResponseEntity<RestExceptionDto> unauthorized(HttpServletRequest request, Throwable e) {
        return handleThrowable(request, e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            cz.inqool.eas.common.exception.ExtensionNotAllowedException.class,
            ExtensionNotAllowedException.class,
            ForbiddenObject.class,
            ForbiddenOperation.class
    })
    public ResponseEntity<RestExceptionDto> forbidden(HttpServletRequest request, Throwable e) {
        if (eventService != null) {
            eventService.create(eventBuilder.forbiddenUrl(request.getRequestURI(), UserGenerator.generateValue()));
        }

        return handleThrowable(request, e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            MissingObject.class
    })
    public ResponseEntity<RestExceptionDto> notFound(HttpServletRequest request, Throwable e) {
        return handleThrowable(request, e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            ConflictObject.class
    })
    public ResponseEntity<RestExceptionDto> conflict(HttpServletRequest request, Throwable e) {
        return handleThrowable(request, e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            ResponseStatusException.class
    })
    public ResponseEntity<RestExceptionDto> responseStatusException(HttpServletRequest request, ResponseStatusException e) {
        return handleThrowable(request, e, e.getStatus());
    }

    /**
     * Log ClientAbortException differently (without returning value - sending a message response) as it means, the
     * client application closes the connection before server send the response, which is not a fault of the server.
     */
    @ExceptionHandler({
            ClientAbortException.class
    })
    public void clientAbortException(HttpServletRequest request, Throwable e) {
        handleThrowable(request, e, HttpStatus.CONTINUE);
    }

    private ResponseEntity<RestExceptionDto> handleThrowable(HttpServletRequest request, Throwable e, HttpStatus defaultHttpStatus) {
        RestExceptionProcessor<?> exceptionProcessor = getProcessorFor(e);
        return exceptionProcessor.processThrowable(request, e, defaultHttpStatus);
    }

    private RestExceptionProcessor<?> getProcessorFor(Throwable throwable) {
        return processorMap.computeIfAbsent(throwable.getClass(), throwableClass -> processors.stream()
                .filter(processor -> processor.getSuitabilityFor(throwableClass) >= 0)
                .min(Comparator.comparingInt(processor -> processor.getSuitabilityFor(throwableClass)))
                .orElseThrow(() -> new RuntimeException("No converter found for: " + throwableClass.getName())));
    }


    @Autowired(required = false)
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @Autowired(required = false)
    public void setEventBuilder(EventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
    }

    @Autowired
    public void setProcessors(List<RestExceptionProcessor<?>> processors) {
        this.processors = processors;
    }
}
