package cz.inqool.eas.common.security.captcha.exception;

import org.springframework.security.core.AuthenticationException;

public final class ReCaptchaInvalidException extends AuthenticationException {

    private static final long serialVersionUID = 5861310537366287163L;

    public ReCaptchaInvalidException(String msg, Throwable t) {
        super(msg, t);
    }

    public ReCaptchaInvalidException(String msg) {
        super(msg);
    }
}