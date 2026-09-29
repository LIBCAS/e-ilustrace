package cz.inqool.eas.common.security.saml;

import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.security.User;
import cz.inqool.eas.common.security.internal.RedirectStrategy;
import cz.inqool.eas.common.security.personal.PersonalEventService;
import cz.inqool.eas.common.security.saml.internal.Saml2Authentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.OpenSamlAuthenticationProvider;
import org.springframework.security.saml2.provider.service.registration.*;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration.AssertingPartyDetails;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Saml2 web security configuration
 *
 * override abstract methods to initialize {@link RelyingPartyRegistration}
 * and create ResponseToken to AbstractAuthenticationToken converter
 *
 * example can be found in CRZP project
 *
 * note: only work if only one RelyingPartyRegistration with one signing certificate
 * but still can be used as example :-)
 */
@Slf4j
public abstract class SamlSecurityConfig extends WebSecurityConfigurerAdapter {
    private PersonalEventService personalEventService;

    private EventService eventService;

    private EventBuilder eventBuilder;

    private SessionRegistry sessionRegistry;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        OpenSamlAuthenticationProvider provider = new OpenSamlAuthenticationProvider();
        provider.setResponseAuthenticationConverter(this.getResponseAuthenticationConverter());
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RedirectStrategy redirectStrategy = new RedirectStrategy();
        redirectStrategy.setContextRelative(true);

        Converter<HttpServletRequest, RelyingPartyRegistration> relyingPartyRegistrationResolver =
                new DefaultRelyingPartyRegistrationResolver(registrationId -> {
                    // This relying party registration repository delegates the request to a bean found during runtime.
                    // Its needed, because the RelyingPartyRegistrationRepository is accessible only after the security filter chain is built.
                    // It might not be very fast, but it's not called on a regular basis.
                    ApplicationContext context = http.getSharedObject(ApplicationContext.class);
                    RelyingPartyRegistrationRepository repository = context.getBean(RelyingPartyRegistrationRepository.class);

                    return repository.findByRegistrationId(registrationId);
                });
        Saml2MetadataFilter filter = new Saml2MetadataFilter(
                relyingPartyRegistrationResolver,
                new OpenSamlMetadataResolver());

        http
                .requestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher(getAuthUrl()),
                        new AntPathRequestMatcher("/saml2/**"),
                        new AntPathRequestMatcher("/login/saml2/sso/**")
                ))
                .sessionManagement().sessionConcurrency(c -> c.sessionRegistry(sessionRegistry)).and()
                .csrf().disable()
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().authenticated()
                )
                .exceptionHandling()
                .and()
                .saml2Login()
                    .authenticationManager(this.authenticationManager())
                    .successHandler((request, response, authentication) -> {
                        User user = (User) authentication.getPrincipal();
                        log.info("{} logged in.", user);

                        if (personalEventService != null) {
                            personalEventService.saveLoginSuccessEvent(new UserReference(user.getId(), user.getName()));
                        }
                        if (eventService != null) {
                            eventService.create(eventBuilder.successfulLogin(user));
                        }

                        redirectStrategy.sendRedirect(request, response, getSuccessRedirectUrl());
                    })
                    .failureHandler((request, response, exception) -> {
                        if (eventService != null) {
                            eventService.create(eventBuilder.failedLogin(null));
                        }

                        log.info("User failed to log in. ({})", exception.getMessage());
                        log.debug("Exception: ", exception);

                        redirectStrategy.sendRedirect(request, response, getErrorRedirectUrl());
                    })
                .and()
                .addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class);
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrations() {
        RelyingPartyRegistration.Builder builder = RelyingPartyRegistrations
                .fromMetadataLocation(getFederationMetadataLocation());

        builder
                .registrationId(getRegistrationId())
                .entityId(getEntityId())
                .assertionConsumerServiceBinding(getMessageBinding())
                .assertingPartyDetails(modifyAssertionPartyDetails());

        List<Saml2X509Credential> credentials = getCredentials();
        if (credentials != null) {
            builder.signingX509Credentials((c) ->
                    c.addAll(credentials.stream()
                            .filter(Saml2X509Credential::isSigningCredential)
                            .collect(Collectors.toList())));
            builder.decryptionX509Credentials((c) ->
                    c.addAll(credentials.stream()
                            .filter(Saml2X509Credential::isDecryptionCredential)
                            .collect(Collectors.toList())));
        }

        String consumerServiceLocation = getConsumerServiceLocation();
        if (consumerServiceLocation != null) {
            builder.assertionConsumerServiceLocation(consumerServiceLocation);
        }

        return new InMemoryRelyingPartyRegistrationRepository(builder.build());
    }

    protected abstract String getEntityId();
    protected abstract String getRegistrationId();

    /**
     * Url where IDP metadata can be found.
     *
     * example: https://t-iam.env.cz/cas/idp/metadata
     */
    protected abstract String getFederationMetadataLocation();

    /**
     * Url to which user will be redirected after login (with saml token).
     *
     * Can be null, if can be determined automatically by accessing metadata url.
     */
    protected String getConsumerServiceLocation() {
        return null;
    }

    /**
     * X509 credentials used for signing or decrypting AuthenticationRequest.
     *
     * Can be null if neither signing nor decrypting is necessary.
     */
    protected List<Saml2X509Credential> getCredentials() {
        return null;
    }

    /**
     * SAML message binding {@code POST} or {@code REDIRECT}.
     *
     * Defaults to POST.
     */
    protected Saml2MessageBinding getMessageBinding() {
        return Saml2MessageBinding.POST;
    }

    /**
     * Override this if you need to change some {@link AssertingPartyDetails} settings.
     *
     * Usually not necessary because it is derived from IPD metadata
     * @see {@link this#getFederationMetadataLocation()}
     */
    protected Consumer<AssertingPartyDetails.Builder> modifyAssertionPartyDetails() {
        return adp -> {};
    }

    protected abstract String getAuthUrl();
    protected abstract String getSuccessRedirectUrl();
    protected abstract String getErrorRedirectUrl();
    protected abstract String getSuccessLogoutRedirectUrl();

    /**
     * Convert {@link OpenSamlAuthenticationProvider.ResponseToken} to {@link AbstractAuthenticationToken}
     * {@link Saml2Authentication}
     */
    protected abstract Converter<OpenSamlAuthenticationProvider.ResponseToken, Saml2Authentication> getResponseAuthenticationConverter();

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
}
