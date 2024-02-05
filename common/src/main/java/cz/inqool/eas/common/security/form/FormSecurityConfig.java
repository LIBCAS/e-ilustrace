package cz.inqool.eas.common.security.form;

import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.security.User;
import cz.inqool.eas.common.security.captcha.CaptchaValidator;
import cz.inqool.eas.common.security.form.internal.FormLoginConfigurer;
import cz.inqool.eas.common.security.form.internal.FormLoginEntryPoint;
import cz.inqool.eas.common.security.form.internal.UsernamePasswordAuthenticationFilter;
import cz.inqool.eas.common.security.form.internal.UsernamePasswordCaptchaAuthenticationFilter;
import cz.inqool.eas.common.security.internal.CombinedFailureHandler;
import cz.inqool.eas.common.security.internal.CombinedSuccessHandler;
import cz.inqool.eas.common.security.internal.RedirectStrategy;
import cz.inqool.eas.common.security.personal.PersonalEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * TODO: comment
 */
@Slf4j
public abstract class FormSecurityConfig extends WebSecurityConfigurerAdapter {
    private CaptchaValidator captchaValidator;

    private PersonalEventService personalEventService;

    private EventService eventService;

    private EventBuilder eventBuilder;

    private SessionRegistry sessionRegistry;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        log.info("Initializing FormSecurityConfig for " + this.getClass().getName());
        auth.authenticationProvider(this.getAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RedirectStrategy redirectStrategy = new RedirectStrategy();
        redirectStrategy.setContextRelative(true);

        FormLoginEntryPoint entryPoint = new FormLoginEntryPoint(this.getLoginPage());
        entryPoint.setRedirectStrategy(redirectStrategy);

        SimpleUrlAuthenticationFailureHandler failureHandler = getAuthenticationFailureHandler();
        failureHandler.setRedirectStrategy(redirectStrategy);

        UsernamePasswordAuthenticationFilter authenticationFilter;
        if (captchaValidator != null) {
            log.info("Enabling captcha validator");
            authenticationFilter = UsernamePasswordCaptchaAuthenticationFilter
                    .builder()
                    .validator(captchaValidator)
                    .action(getCaptchaAction())
                    .build();
        } else {
            authenticationFilter = new UsernamePasswordAuthenticationFilter();
        }

        http
                .requestMatcher(new AntPathRequestMatcher(this.getAuthUrl()))
                .sessionManagement().sessionConcurrency(c -> c.sessionRegistry(sessionRegistry)).and()
                .csrf().disable()
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().authenticated()
                )
                .exceptionHandling().authenticationEntryPoint(entryPoint).and()
                .apply(new FormLoginConfigurer<>())
                    .authenticationFilter(authenticationFilter)
                    .loginPage(this.getLoginPage())
                    .failureHandler(new CombinedFailureHandler(
                            failureHandler,
                            (request, response, exception) -> {
                                String username = authenticationFilter.obtainUsername(request);

                                if (username != null) {
                                    User user = getAuthenticationProvider().findUser(username);

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
                    ))
                    .loginProcessingUrl(this.getAuthUrl());
    }

    protected SimpleUrlAuthenticationFailureHandler getAuthenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler(this.getFailureRedirectUrl());
    }

    @Override
    @Bean("authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired(required = false)
    public void setCaptchaValidator(CaptchaValidator captchaValidator) {
        this.captchaValidator = captchaValidator;
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

    @Autowired
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Override if default CAPTCHA action is not suitable.
     * @return
     */
    protected String getCaptchaAction() {
        return "login";
    }

    protected abstract String getLoginPage();
    protected abstract String getSuccessRedirectUrl();
    protected abstract String getFailureRedirectUrl();
    protected abstract String getAuthUrl();

    protected abstract <T extends AuthenticationProvider & FormUserDetailProvider> T getAuthenticationProvider();
}
