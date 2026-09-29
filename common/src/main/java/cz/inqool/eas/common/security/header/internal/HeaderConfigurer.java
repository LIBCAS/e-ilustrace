package cz.inqool.eas.common.security.header.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;


public class HeaderConfigurer<B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<HeaderConfigurer<B>, B> {
    @Getter
    @Setter
    private HeaderAuthenticationFilter authFilter = new HeaderAuthenticationFilter();

    private AuthenticationFailureHandler failureHandler;

    private AuthenticationSuccessHandler successHandler;

    private boolean checkForPrincipalChanges;

    private boolean invalidateSessionOnPrincipalChange = true;

    private String principalRequestHeader ="SM_USER";

    @Override
    public void init(B builder) throws Exception {
        super.init(builder);
    }

    @Override
    public void configure(B http) {
        this.authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        this.authFilter.setAuthenticationSuccessHandler(this.successHandler);
        this.authFilter.setAuthenticationFailureHandler(this.failureHandler);
        this.authFilter.setCheckForPrincipalChanges(this.checkForPrincipalChanges);
        this.authFilter.setPrincipalRequestHeader(this.principalRequestHeader);
        this.authFilter.setInvalidateSessionOnPrincipalChange(this.invalidateSessionOnPrincipalChange);
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            this.authFilter.setSessionStrategy(sessionAuthenticationStrategy);
        }

        HeaderAuthenticationFilter filter = postProcess(this.authFilter);

        http.addFilterAfter(filter, LogoutFilter.class);
        http.addFilterAfter((request, response, chain) -> {
            // do nothing, so this request ends here
        }, HeaderAuthenticationFilter.class);
    }

    /**
     * Custom authentication filter.
     */
    public HeaderConfigurer<B> authenticationFilter(HeaderAuthenticationFilter filter) {
        setAuthFilter(filter);
        return this;
    }

    /**
     * Specifies the {@link AuthenticationFailureHandler} to use when authentication
     * fails. The default is redirecting to "/login?error" using
     * {@link SimpleUrlAuthenticationFailureHandler}
     * @param authenticationFailureHandler the {@link AuthenticationFailureHandler} to use
     * when authentication fails.
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final HeaderConfigurer<B> failureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return this;
    }

    /**
     * Specifies the {@link AuthenticationSuccessHandler} to be used. The default is
     * {@link SavedRequestAwareAuthenticationSuccessHandler} with no additional properties
     * set.
     * @param successHandler the {@link AuthenticationSuccessHandler}.
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final HeaderConfigurer<B> successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public final HeaderConfigurer<B> checkForPrincipalChanges(boolean checkForPrincipalChanges) {
        this.checkForPrincipalChanges = checkForPrincipalChanges;
        return this;
    }

    public final HeaderConfigurer<B> invalidateSessionOnPrincipalChange(boolean invalidateSessionOnPrincipalChange) {
        this.invalidateSessionOnPrincipalChange = invalidateSessionOnPrincipalChange;
        return this;
    }

    public final HeaderConfigurer<B> principalRequestHeader(String principalRequestHeader) {
        this.principalRequestHeader = principalRequestHeader;
        return this;
    }
}
