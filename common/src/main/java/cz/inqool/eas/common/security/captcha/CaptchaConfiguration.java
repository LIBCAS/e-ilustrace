package cz.inqool.eas.common.security.captcha;

import org.springframework.context.annotation.Bean;

public abstract class CaptchaConfiguration {
    @Bean
    public CaptchaAttemptService captchaAttemptService() {
        CaptchaAttemptService.CaptchaAttemptServiceBuilder builder = CaptchaAttemptService.builder();
        configure(builder);

        return builder.build();
    }

    @Bean
    public CaptchaValidator captchaValidator() {
        CaptchaValidator.CaptchaValidatorBuilder builder = CaptchaValidator.builder();
        configure(builder);

        return builder.build();
    }

    /**
     * Method for configuration the {@link CaptchaAttemptService}.
     *
     * @param builder Builder used for configuration
     */
    protected abstract void configure(CaptchaAttemptService.CaptchaAttemptServiceBuilder builder);

    /**
     * Method for configuration the {@link CaptchaValidator}.
     *
     * @param builder Builder used for configuration
     */
    protected abstract void configure(CaptchaValidator.CaptchaValidatorBuilder builder);
}
