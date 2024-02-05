package cz.inqool.eas.common.admin.console.stream;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.security.Principal;

/**
 * Byte stream that sends buffered values to defined web socket destination.
 */
public class WebSocketOutputStream extends ByteArrayOutputStream {

    private final SimpMessagingTemplate webSocket;
    private final String destination;
    private final Principal principal;


    public WebSocketOutputStream(SimpMessagingTemplate webSocket, String destination) {
        this(webSocket, destination, null);
    }

    public WebSocketOutputStream(SimpMessagingTemplate webSocket, String destination, @Nullable Principal principal) {
        this.webSocket = webSocket;
        this.destination = destination;
        this.principal = principal;
    }


    @Override
    public void flush() {
        if (principal != null) {
            webSocket.convertAndSendToUser(principal.getName(), destination, toString());
        } else {
            webSocket.convertAndSend(destination, toString());
        }

        reset();
    }
}
