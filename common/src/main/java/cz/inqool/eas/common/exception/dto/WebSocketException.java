package cz.inqool.eas.common.exception.dto;

/**
 * Class representing an exception raised during handling of a web socket message.
 */
public class WebSocketException extends BaseException {

    public WebSocketException(Throwable exception) {
        super(exception);
    }
}
