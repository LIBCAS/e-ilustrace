package cz.inqool.eas.eil.security;

import cz.inqool.eas.common.security.LogoutConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EilLogoutConfiguration extends LogoutConfiguration {
    @Override
    protected String getLogoutUrl() {
        return "/logout";
    }

    @Override
    protected String getSuccessRedirectUrl() {
        return "/";
    }
}
