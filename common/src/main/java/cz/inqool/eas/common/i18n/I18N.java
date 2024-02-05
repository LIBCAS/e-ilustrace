package cz.inqool.eas.common.i18n;

import cz.inqool.eas.common.i18n.yml.YamlLocalizationConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

/**
 * Internationalization (i18n)
 *
 * This class provides access to localized messages configured in project resources folder.
 *
 * @see YamlLocalizationConfig#messageSourceProperties()
 * @see MessageSourceProperties
 */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I18N implements ApplicationListener<ApplicationReadyEvent> {

    private static MessageSource messageSource;


    /**
     * Get message using default locale.
     *
     * @param key  message key
     * @param args message arguments (used to format the message and provide custom values)
     * @return localized message
     */
    public static String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(locale, key, args);
    }

    /**
     * Get message using provided locale.
     *
     * @param key  message key
     * @param args message arguments (used to format the message and provide custom values)
     * @return localized message
     */
    public static String getMessage(Locale locale, String key, Object... args) {
        notNull(messageSource, () -> new RuntimeException("Application not properly initialized yet."));
        return messageSource.getMessage(key, args, locale);
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        messageSource = event.getApplicationContext().getBean(MessageSource.class);
    }
}
