package cz.inqool.eas.common.security.captcha;

import java.net.URI;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import cz.inqool.eas.common.security.captcha.exception.ReCaptchaInvalidException;
import cz.inqool.eas.common.security.captcha.exception.ReCaptchaUnavailableException;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Slf4j
public class CaptchaValidator {
    protected CaptchaAttemptService captchaAttemptService;

    protected String secretKey;

    protected Float threshold;

    protected static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    protected static final String RECAPTCHA_URL_TEMPLATE = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

    @Builder
    public CaptchaValidator(String secretKey, Float threshold) {
        this.secretKey = secretKey;
        this.threshold = threshold;
    }

    public void processCaptcha(HttpServletRequest request, String captcha, final String action) throws ReCaptchaInvalidException {
        securityCheck(request, captcha);

        final URI verifyUri = URI.create(String.format(RECAPTCHA_URL_TEMPLATE, secretKey, captcha, getClientIP(request)));
        try {
            RestTemplate restTemplate = new RestTemplate();
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
            log.debug("Google's captcha: {} ", googleResponse.toString());

            if (!googleResponse.isSuccess() || !googleResponse.getAction().equals(action) || googleResponse.getScore() < threshold) {
                if (googleResponse.hasClientError()) {
                    captchaAttemptService.reCaptchaFailed(getClientIP(request));
                }
                throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
            }
        } catch (RestClientException rce) {
            throw new ReCaptchaUnavailableException("Registration unavailable at this time.  Please try again later.", rce);
        }
        captchaAttemptService.reCaptchaSucceeded(getClientIP(request));
    }

    protected void securityCheck(HttpServletRequest request, final String response) {
        log.debug("Attempting to validate response {}", response);

        if (captchaAttemptService.isBlocked(getClientIP(request))) {
            throw new ReCaptchaInvalidException("Client exceeded maximum number of failed attempts");
        }

        if (!responseSanityCheck(response)) {
            throw new ReCaptchaInvalidException("Response contains invalid characters");
        }
    }

    protected boolean responseSanityCheck(final String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    protected String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Autowired
    public void setCaptchaAttemptService(CaptchaAttemptService captchaAttemptService) {
        this.captchaAttemptService = captchaAttemptService;
    }
}