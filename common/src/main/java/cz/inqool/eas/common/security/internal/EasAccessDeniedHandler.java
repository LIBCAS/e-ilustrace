package cz.inqool.eas.common.security.internal;

import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.authored.user.UserGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class EasAccessDeniedHandler extends AccessDeniedHandlerImpl {
    private EventService eventService;

    private EventBuilder eventBuilder;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        super.handle(request, response, accessDeniedException);

        if (eventService != null) {
            String url = request.getMethod() + ": " + request.getRequestURI();
            eventService.create(eventBuilder.forbiddenUrl(url, UserGenerator.generateValue()));
        }
    }

    @Autowired(required = false)
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @Autowired(required = false)
    public void setEventBuilder(EventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
    }
}
