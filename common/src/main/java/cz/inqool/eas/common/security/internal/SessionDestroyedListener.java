package cz.inqool.eas.common.security.internal;

import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.security.User;
import cz.inqool.eas.common.security.personal.PersonalEventService;
import cz.inqool.eas.common.security.service.ServiceHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;


@Slf4j
@Component
public class SessionDestroyedListener implements ApplicationListener<SessionDestroyedEvent> {
    private PersonalEventService personalEventService;

    private EventService eventService;

    private EventBuilder eventBuilder;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event)
    {
        List<SecurityContext> lstSecurityContext = event.getSecurityContexts();

        for (SecurityContext securityContext : lstSecurityContext)
        {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();

                if (event instanceof HttpSessionDestroyedEvent && authentication.isAuthenticated()) {
                    HttpSession session = ((HttpSessionDestroyedEvent) event).getSession();

                    Object isImpersonating = session.getAttribute(ServiceHub.IMPERSONATING_SESSION_KEY);

                    if (!Objects.equals(isImpersonating, Boolean.TRUE)) {
                        // fired also in case of logout
                        /*
                        log.info("{} has logout.", user);


                        if (personalEventService != null) {
                            personalEventService.saveLogoutAutomaticEvent(new UserReference(user.getId(), user.getName()));
                        }
                        if (eventService != null) {
                            eventService.create(eventBuilder.logoutAutomatic(user));
                        }*/
                    } else {
                        log.trace("Service hub session for {} has logout.", user);
                    }
                }
            }
        }
    }

    @Autowired(required = false)
    public void setPersonalEventService(PersonalEventService personalEventService) {
        this.personalEventService = personalEventService;
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
