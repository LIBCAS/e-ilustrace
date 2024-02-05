package cz.inqool.eas.common.security;

import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.security.internal.RedirectStrategy;
import cz.inqool.eas.common.security.personal.PersonalEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Order(50)
public abstract class LogoutConfiguration extends WebSecurityConfigurerAdapter {
    private PersonalEventService personalEventService;
    private EventService eventService;
    private EventBuilder eventBuilder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatcher(new AntPathRequestMatcher(getLogoutUrl()))
                .csrf().disable()
                .authorizeRequests().anyRequest().permitAll().and()
                .logout()
                    .logoutSuccessHandler((request, response, authentication) -> {
                            if (authentication != null) {
                                Object principal = authentication.getPrincipal();
                                authentication.setAuthenticated(false);

                                if (principal instanceof User) {
                                    User user = (User) principal;
                                    log.info("{} has logout.", user);

                                    request.getSession().setAttribute("EAS_INVALID", true);

                                    if (personalEventService != null) {
                                        if (request.getParameterMap().containsKey("automatic")) {
                                            personalEventService.saveLogoutAutomaticEvent(new UserReference(user.getId(), user.getName()));
                                        } else {
                                            personalEventService.saveLogoutEvent(new UserReference(user.getId(), user.getName()));
                                        }
                                    }
                                    if (eventService != null) {
                                        if (request.getParameterMap().containsKey("automatic")) {
                                            eventService.create(eventBuilder.logoutAutomatic(user));
                                        } else {
                                            eventService.create(eventBuilder.logout(user));
                                        }
                                    }
                                }
                            }
                            RedirectStrategy redirectStrategy = new RedirectStrategy();
                            String redirect = getSuccessRedirectUrl();
                            redirectStrategy.setContextRelative(!redirect.startsWith("http"));
                            redirectStrategy.sendRedirect(request, response, redirect);
                    });
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

    protected abstract String getLogoutUrl();
    protected abstract String getSuccessRedirectUrl();
}
