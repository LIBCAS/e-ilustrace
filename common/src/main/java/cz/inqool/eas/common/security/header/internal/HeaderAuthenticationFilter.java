package cz.inqool.eas.common.security.header.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Copy of {@link org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter} with exposed header extractor.
 */
public class HeaderAuthenticationFilter extends org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter {
    @Getter
    @Setter
    private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();

    /**
     * Read and returns the header named by {@code principalRequestHeader} from the
     * request.
     * @throws PreAuthenticatedCredentialsNotFoundException if the header is missing and
     * {@code exceptionIfHeaderMissing} is set to {@code true}.
     */

    public String obtainUsername(HttpServletRequest request) {
        try {
            return (String) this.getPreAuthenticatedPrincipal(request);
        } catch (PreAuthenticatedCredentialsNotFoundException ex) {
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
        this.sessionStrategy.onAuthentication(authResult, request, response);

        super.successfulAuthentication(request, response, authResult);
    }
}
