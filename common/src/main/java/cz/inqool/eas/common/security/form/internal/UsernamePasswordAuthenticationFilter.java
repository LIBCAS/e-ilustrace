package cz.inqool.eas.common.security.form.internal;

import javax.servlet.http.HttpServletRequest;

/**
 * Copy of {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter} with exposed username extractor.
 */

public class UsernamePasswordAuthenticationFilter extends org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter {
    @Override
    public String obtainPassword(HttpServletRequest request) {
        return super.obtainPassword(request);
    }

    @Override
    public String obtainUsername(HttpServletRequest request) {
        return super.obtainUsername(request);
    }
}
