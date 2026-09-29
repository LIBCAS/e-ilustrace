package cz.inqool.eas.common.exception.handler;

import cz.inqool.eas.common.exception.dto.WebSocketException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Class handling exceptions raised during WebSocket endpoint executions.
 *
 * Provides unified and more detailed information about exceptions.
 */
@Slf4j
@ControllerAdvice
public class WebSocketExceptionHandler {

    @MessageExceptionHandler({
            Exception.class
    })
    public WebSocketException exception(Exception e) {
        WebSocketException error = new WebSocketException(e);
        String msg = "\n" + error.toString();
        log.error(msg, e);
        return error;
    }
}
