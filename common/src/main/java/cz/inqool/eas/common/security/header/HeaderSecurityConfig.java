package cz.inqool.eas.common.security.header;

import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.security.User;
import cz.inqool.eas.common.security.header.internal.HeaderAuthenticationFilter;
import cz.inqool.eas.common.security.header.internal.HeaderConfigurer;
import cz.inqool.eas.common.security.internal.RedirectStrategy;
import cz.inqool.eas.common.security.internal.CombinedFailureHandler;
import cz.inqool.eas.common.security.internal.CombinedSuccessHandler;
import cz.inqool.eas.common.security.personal.PersonalEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * TODO: comment
 */
@Slf4j
public abstract class HeaderSecurityConfig extends WebSecurityConfigurerAdapter {
    private PersonalEventService personalEventService;

    private SessionRegistry sessionRegistry;

    private EventService eventService;

    private EventBuilder eventBuilder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(this.getAuthenticationUserDetailsService());
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RedirectStrategy redirectStrategy = new RedirectStrategy();
        redirectStrategy.setContextRelative(true);

        HeaderAuthenticationFilter authenticationFilter = new HeaderAuthenticationFilter();

        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler(this.getFailureRedirectUrl());
        failureHandler.setRedirectStrategy(redirectStrategy);

        http
                .requestMatcher(new AntPathRequestMatcher(getAuthUrl()))
                .sessionManagement().sessionConcurrency(c -> c.sessionRegistry(sessionRegistry)).and()
                .csrf().disable()
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().authenticated()
                )
                .exceptionHandling().and()
                .apply(new HeaderConfigurer<>())
                    .authenticationFilter(authenticationFilter)
                    .checkForPrincipalChanges(true)
                    .invalidateSessionOnPrincipalChange(true)
                    .principalRequestHeader(this.getUsernameHeader())
                    .failureHandler(new CombinedFailureHandler(
                            failureHandler,
                            (request, response, exception) -> {
                                String username = authenticationFilter.obtainUsername(request);

                                if (username != null) {
                                    User user = getAuthenticationUserDetailsService().findUser(username);

                                    if (user != null) {
                                        if (personalEventService != null) {
                                            personalEventService.saveLoginFailedEvent(new UserReference(user.getId(), user.getName()));
                                        }
                                        if (eventService != null) {
                                            eventService.create(eventBuilder.failedLogin(user));
                                        }
                                        log.info("{} failed to log in.", user);
                                    } else {
                                        if (eventService != null) {
                                            eventService.create(eventBuilder.failedLogin(null));
                                        }
                                        log.info("Username {} does not exist.", username);
                                    }
                                } else {
                                    if (eventService != null) {
                                        eventService.create(eventBuilder.failedLogin(null));
                                    }
                                    log.info("No username param was specified");
                                }
                            }
                    ))
                    .successHandler(new CombinedSuccessHandler(
                            (request, response, authentication) -> {
                                User user = (User) authentication.getPrincipal();
                                log.info("{} logged in.", user);

                                if (personalEventService != null) {
                                    personalEventService.saveLoginSuccessEvent(new UserReference(user.getId(), user.getName()));
                                }
                                if (eventService != null) {
                                    eventService.create(eventBuilder.successfulLogin(user));
                                }

                                redirectStrategy.sendRedirect(request, response, getSuccessRedirectUrl());
                            }
                    ));
    }

    @Autowired(required = false)
    public void setPersonalEventService(PersonalEventService personalEventService) {
        this.personalEventService = personalEventService;
    }

    @Autowired
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Autowired(required = false)
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @Autowired(required = false)
    public void setEventBuilder(EventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
    }

    protected abstract String getAuthUrl();

    protected abstract String getSuccessRedirectUrl();

    protected abstract String getFailureRedirectUrl();

    protected abstract String getUsernameHeader();

    protected abstract HeaderAuthenticationProvider getAuthenticationUserDetailsService();
}
