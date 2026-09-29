package cz.inqool.eas.common.exception.v2.rest.processor;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.v2.EasException;
import cz.inqool.eas.common.exception.v2.ExceptionConfigurationProperties;
import cz.inqool.eas.common.exception.v2.ExceptionConfigurationProperties.HandlerProperties.RestProperties;
import cz.inqool.eas.common.exception.v2.LogStrategy;
import cz.inqool.eas.common.exception.v2.rest.RestExceptionHandler;
import cz.inqool.eas.common.exception.v2.rest.RestExceptionUtils;
import cz.inqool.eas.common.exception.v2.rest.dto.RestExceptionDto;
import cz.inqool.eas.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.CollectionUtils.toMultiValueMap;

/**
 * Abstract exception processor class for exceptions of type {@link T}.
 *
 * @param <T> type of handled throwable
 */
@Beta
public abstract class RestExceptionProcessor<T extends Throwable> {

    protected static final Logger log = org.slf4j.LoggerFactory.getLogger(RestExceptionHandler.class);
    protected static final HttpHeaders HEADERS = new HttpHeaders(toMultiValueMap(Map.of(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))));
    private static final Map<Class<? extends Throwable>, LogStrategy> LOG_STRATEGY_CACHE = new HashMap<>();

    /**
     * REST exception handler configuration properties
     */
    protected RestProperties properties;


    /**
     * Initializes this processor, loads configuration into the cache
     */
    @PostConstruct
    public void init() {
        // load pre-configured log strategies into the cache
        LOG_STRATEGY_CACHE.putAll(properties.getLog().getStrategies());
    }

    /**
     * Computes suitability of this processor for given throwable class. Used to choose most suitable processor for a
     * particular exception. Smallest non-negative value represents the best match for {@code throwableClass}
     *
     * @param throwableClass type of throwable to check suitability for
     * @return {@code 0} if the processor is suited for {@code throwableClass}, any positive value (representing depth
     * of traversal to {@code throwableClass} in case of subclasses), or {@code -1} if this processor is not suitable
     * for processing of {@code throwableClass},
     * @see RestExceptionUtils#getDistance(Class, Class)
     */
    public int getSuitabilityFor(@NonNull Class<? extends Throwable> throwableClass) {
        return RestExceptionUtils.getDistance(getProcessedType(), throwableClass);
    }

    /**
     * Determines logging strategy for given processor, considering global strategy configuration.
     */
    protected LogStrategy getLogStrategy(@NonNull T throwable) {
        return LOG_STRATEGY_CACHE.computeIfAbsent(throwable.getClass(), throwableClass -> LOG_STRATEGY_CACHE.keySet().stream()
                .filter(mappedClass -> RestExceptionUtils.getDistance(mappedClass, throwableClass) >= 0)
                .min(Comparator.comparingInt(mappedClass -> RestExceptionUtils.getDistance(mappedClass, throwableClass)))
                .map(LOG_STRATEGY_CACHE::get)
                .orElse(properties.getLog().getDefaultStrategy()));
    }

    /**
     * Log exception in standardized format, considering specified {@link LogStrategy} (see {@link
     * #getLogStrategy(Throwable)}). To customize log messages, override {@link #getBriefMessage(Throwable)} and/or
     * {@link #getDetailedMessage(Throwable, String, UserReference)} to provide more specific messages (or do not use
     * this method at all)
     *
     * @param throwable exception thrown
     * @param urlPath   endpoint path that throwed given {@code throwable}
     * @param ipAddress client IP address
     * @param user      reference to the user who made the request (optional)
     */
    protected void logError(@NonNull T throwable, @NonNull String urlPath, @Nullable String ipAddress, @Nullable UserReference user) {
        LogStrategy logStrategy = getLogStrategy(throwable);

        switch (logStrategy) {
            case ALL:
                log.error(getDetailedMessage(throwable, urlPath, ipAddress, user), throwable);
                break;
            case BRIEF:
                log.error(getBriefMessage(throwable));
                break;
            case BRIEF_WITH_STACKTRACE:
                log.error(getBriefMessage(throwable), throwable);
                break;
            case DETAILED:
                log.error(getDetailedMessage(throwable, urlPath, ipAddress, user));
                break;
            case NONE:
                break;
            default:
                throw new IllegalArgumentException("Log strategy not supported: " + logStrategy);
        }
    }

    /**
     * Returns only short exception message to be used in logging.
     *
     * @param throwable exception thrown
     */
    protected String getBriefMessage(@NonNull T throwable) {
        return throwable.toString();
    }

    /**
     * Returns detailed exception message with additional information (similar as in {@link EasException#details} or
     * {@link EasException#debugInfo}) for better exception resoultion.
     *
     * @param throwable     exception thrown
     * @param urlPath       endpoint path that throwed given {@code throwable}
     * @param ipAddress     client IP address
     * @param userReference reference to the user who made the request (optional)
     */
    protected String getDetailedMessage(@NonNull T throwable, @NonNull String urlPath, @Nullable String ipAddress, @Nullable UserReference userReference) {
        StringBuilder msgBuilder = new StringBuilder(throwable.toString());
        msgBuilder.append("\n\t").append("endpoint = ").append(urlPath);
        if (properties.isLogIpAddress()) {
            ifPresent(ipAddress, address -> msgBuilder.append("\n\t").append("IP = ").append(address));
        }
        ifPresent(userReference, user -> msgBuilder.append("\n\t").append("user = ").append(JsonUtils.toJsonString(user)));

        return msgBuilder.toString();
    }

    /**
     * Returns type of throwable handled with this processor.
     */
    protected abstract Class<T> getProcessedType();

    /**
     * Process given throwable, returning a {@link ResponseEntity} to be used in HTTP response.
     *
     * @see #processTypedThrowable(HttpServletRequest, Throwable, HttpStatus)
     */
    public final ResponseEntity<RestExceptionDto> processThrowable(@NonNull HttpServletRequest request, @NonNull Throwable throwable, @NonNull HttpStatus defaultHttpStatus) {
        //noinspection unchecked
        return processTypedThrowable(request, (T) throwable, defaultHttpStatus);
    }

    /**
     * Process given throwable, returning a {@link ResponseEntity} to be used in HTTP response.
     *
     * @param request           request throwing given {@code throwable}
     * @param throwable         an exception thrown during {@code request} handling
     * @param defaultHttpStatus HTTP status recommended for use for the type of given {@code throwable}
     * @return fully initialized and set {@link ResponseEntity} containing all data to be sent in HTTP response
     */
    protected abstract ResponseEntity<RestExceptionDto> processTypedThrowable(@NonNull HttpServletRequest request, @NonNull T throwable, @NonNull HttpStatus defaultHttpStatus);


    @Autowired
    public void setProperties(ExceptionConfigurationProperties properties) {
        this.properties = properties.getHandler().getRest();
    }
}
