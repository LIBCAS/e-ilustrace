package cz.inqool.eas.eil.security;

import cz.inqool.eas.common.security.form.FormSecurityConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.util.Map;

@Order(102)
@Slf4j
@ConditionalOnProperty(prefix = "eil.security.form", name = "enabled", havingValue = "true")
public class EilFormSecurityConfiguration extends FormSecurityConfig {

    @Getter
    private EilFormAuthenticationProvider authenticationProvider;


    @Override
    protected String getAuthUrl() {
        return "/auth";
    }

    @Override
    protected String getLoginPage() {
        return "/login";
    }

    @Override
    protected String getSuccessRedirectUrl() {
        return "/search";
    }

    @Override
    protected String getFailureRedirectUrl() {
        return "/login?error";
    }

//    @Override
//    protected SimpleUrlAuthenticationFailureHandler getAuthenticationFailureHandler() {
//        ExceptionMappingAuthenticationFailureHandler failureHandler = new ExceptionMappingAuthenticationFailureHandler();
//        failureHandler.setDefaultFailureUrl(getFailureRedirectUrl());
//        failureHandler.setExceptionMappings(Map.ofEntries(
//                Map.entry(LockedException.class.getName(), getFailureRedirectUrl() + "=locked"),
//                Map.entry(DisabledException.class.getName(), getFailureRedirectUrl() + "=inactive")
//        ));
//
//        return failureHandler;
//    }

    @Autowired
    public void setAuthenticationProvider(EilFormAuthenticationProvider provider) {
        this.authenticationProvider = provider;
    }
}
