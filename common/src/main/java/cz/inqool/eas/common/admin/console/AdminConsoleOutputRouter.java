package cz.inqool.eas.common.admin.console;

import cz.inqool.eas.common.admin.console.stream.MultiOutputStream;
import cz.inqool.eas.common.admin.console.stream.WebSocketOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.PrintStream;

@Slf4j
public class AdminConsoleOutputRouter implements ApplicationListener<ContextRefreshedEvent> {
    private SimpMessagingTemplate template;

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        log.debug("Rerouting all log printing to webSocket.");

        PrintStream sOut = System.out;
        PrintStream sErr = System.err;

        WebSocketOutputStream outputStream = new WebSocketOutputStream(template, "/topic/script/log");

        System.setOut(new PrintStream(new MultiOutputStream(sOut, outputStream)));
        System.setErr(new PrintStream(new MultiOutputStream(sErr, outputStream)));
    }

    @Autowired
    public void setTemplate(SimpMessagingTemplate template) {
        this.template = template;
    }
}
