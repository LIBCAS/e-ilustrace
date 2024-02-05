package cz.inqool.eas.common.admin.console;

import cz.inqool.eas.common.admin.console.dto.ExecuteScriptRequest;
import cz.inqool.eas.common.admin.console.dto.ExecuteScriptResponse;
import cz.inqool.eas.common.admin.console.stream.WebSocketOutputStream;
import cz.inqool.eas.common.script.ScriptExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;

import java.security.Principal;

/**
 * This bean should not be created automatically but needs to have Controller annotation.
 */
@ConditionalOnExpression("false")
@Controller
@Slf4j
public class AdminConsoleService {
    private static final String CONSOLE_OUT_MESSAGE_DESTINATION = "/topic/script/execute/out";

    private SimpMessagingTemplate webSocket;

    private ScriptExecutor scriptExecutor;

    @MessageMapping("/script/execute/request")
    @SendToUser("/topic/script/execute/response")
    public ExecuteScriptResponse executeScript(ExecuteScriptRequest message, Principal principal) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            message.setOutputStream(new WebSocketOutputStream(webSocket, CONSOLE_OUT_MESSAGE_DESTINATION, principal));

            Object result = scriptExecutor.executeScript(
                    message.getScriptType(),
                    message.getScript(),
                    message.getUseTransaction(),
                    message.getParams(),
                    message.getOutputStream()
            );

            stopWatch.stop();

            return ExecuteScriptResponse.success(result, stopWatch.getLastTaskTimeMillis());
        } catch (Exception ex) {
            log.error("Error during script execution", ex);
            stopWatch.stop();
            return ExecuteScriptResponse.failure("Error, see console output", stopWatch.getLastTaskTimeMillis());
        }
    }

    @Autowired
    public void setWebSocket(SimpMessagingTemplate webSocket) {
        this.webSocket = webSocket;
    }

    @Autowired
    public void setScriptExecutor(ScriptExecutor scriptExecutor) {
        this.scriptExecutor = scriptExecutor;
    }
}
