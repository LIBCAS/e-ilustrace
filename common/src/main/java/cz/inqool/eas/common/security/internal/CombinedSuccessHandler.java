package cz.inqool.eas.common.security.internal;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Wraps multiple failure handlers and calls them one by one.
 */
public class CombinedSuccessHandler implements AuthenticationSuccessHandler {
    private final Set<AuthenticationSuccessHandler> handlers;

    public CombinedSuccessHandler(AuthenticationSuccessHandler... handlers) {
        this.handlers = new LinkedHashSet<>(Arrays.asList(handlers));
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        for (AuthenticationSuccessHandler handler : handlers) {
            handler.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
