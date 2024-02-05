package cz.inqool.eas.common.security.form.internal;

import cz.inqool.eas.common.security.captcha.CaptchaValidator;
import lombok.Builder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UsernamePasswordCaptchaAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public static final String SPRING_SECURITY_FORM_CAPTCHA_KEY = "captcha";

    private String captchaParameter = SPRING_SECURITY_FORM_CAPTCHA_KEY;

    private final CaptchaValidator validator;

    private final String action;

    @Builder
    public UsernamePasswordCaptchaAuthenticationFilter(CaptchaValidator validator, String action) {
        this.validator = validator;
        this.action = action;
    }

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        // does not support postOnly because its private
        // if (postOnly && !request.getMethod().equals("POST")) {
        //    throw new AuthenticationServiceException(
        //            "Authentication method not supported: " + request.getMethod());
        //}

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String captcha = obtainCaptcha(request);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        if (captcha == null) {
            captcha = "";
        }

        validateCaptcha(request, captcha);

        username = username.trim();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    protected String obtainCaptcha(HttpServletRequest request) {
        return request.getParameter(captchaParameter);
    }

    /**
     * Sets the parameter name which will be used to obtain the captcha from the login
     * request.
     *
     * @param captchaParameter the parameter name. Defaults to "captcha".
     */
    public void setCaptchaParameter(String captchaParameter) {
        Assert.hasText(captchaParameter, "Captcha parameter must not be empty or null");
        this.captchaParameter = captchaParameter;
    }

    protected void validateCaptcha(HttpServletRequest request, String captcha) {
        validator.processCaptcha(request, captcha, action);
    }
}
