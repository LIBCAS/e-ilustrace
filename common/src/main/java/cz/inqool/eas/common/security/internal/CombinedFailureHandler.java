package cz.inqool.eas.common.security.internal;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
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
public class CombinedFailureHandler implements AuthenticationFailureHandler {
    private final Set<AuthenticationFailureHandler> handlers;

    public CombinedFailureHandler(AuthenticationFailureHandler... handlers) {
        this.handlers = new LinkedHashSet<>(Arrays.asList(handlers));
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        for (AuthenticationFailureHandler handler : handlers) {
            handler.onAuthenticationFailure(request, response, exception);
        }
    }
}
