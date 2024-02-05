package cz.inqool.eas.common.exception.v2;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.exception.v2.rest.RestExceptionHandler;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static cz.inqool.eas.common.utils.JsonUtils.toJsonString;

/**
 * Common EAS exception class. Defines format for all custom exceptions raised on project.
 */
@Beta
@Getter
public abstract class EasException extends RuntimeException {

    /**
     * Arbitrary exception code used for automated exception processing. Should be unique with conjunction with {@link
     * #httpStatus} for proper error communication.
     */
    @NonNull
    protected final String code;

    /**
     * Default HTTP status returned in REST error response. Can be overridden with custom value using {@link
     * #status(HttpStatus)}.
     *
     * @see org.springframework.http.HttpStatus
     */
    @NonNull
    protected int httpStatus;

    /**
     * Specifies custom log strategy for this exception
     */
    @Nullable
    protected LogStrategy logStrategy;

    /**
     * Exception details, propagated to client also within production environment. Should contain useful (formatted)
     * information for better exception message construction.
     */
    @Nullable
    protected Object details;

    /**
     * Exception debug details, they're not propagated to client within production environment. Only for debug or
     * logging purposes.
     */
    @Nullable
    protected Object debugInfo;


    protected EasException(@NonNull String code, int httpStatus) {
        this(code, httpStatus, code);
    }

    protected EasException(@NonNull String code, int httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    protected EasException(@NonNull String code, int httpStatus, Throwable cause) {
        this(code, httpStatus, code, cause);
    }

    protected EasException(@NonNull String code, int httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }


    /**
     * Set custom log strategy for this exception (overrides default)
     *
     * @see #logStrategy
     */
    public <E extends EasException> E log(@NonNull LogStrategy strategy) {
        this.logStrategy = strategy;
        //noinspection unchecked
        return (E) this;
    }

    /**
     * @see LogStrategy#BRIEF
     * @see #log(LogStrategy)
     */
    public <E extends EasException> E logBrief() {
        return log(LogStrategy.BRIEF);
    }

    /**
     * @see LogStrategy#BRIEF_WITH_STACKTRACE
     * @see #log(LogStrategy)
     */
    public <E extends EasException> E logBriefWithStacktrace() {
        return log(LogStrategy.BRIEF_WITH_STACKTRACE);
    }

    /**
     * @see LogStrategy#DETAILED
     * @see #log(LogStrategy)
     */
    public <E extends EasException> E logDetailed() {
        return log(LogStrategy.DETAILED);
    }

    /**
     * @see LogStrategy#ALL
     * @see #log(LogStrategy)
     */
    public <E extends EasException> E logAll() {
        return log(LogStrategy.ALL);
    }

    /**
     * @see LogStrategy#NONE
     * @see #log(LogStrategy)
     */
    public <E extends EasException> E logNone() {
        return log(LogStrategy.NONE);
    }

    /**
     * Set custom HTTP status for this exception (should be preferred over that defined in {@link ExceptionHandler}
     * methods in {@link RestExceptionHandler})
     *
     * @see #httpStatus
     */
    public <E extends EasException> E status(@NonNull HttpStatus status) {
        return status(status.value());
    }

    /**
     * Set custom HTTP status for this exception (should be preferred over that defined in {@link ExceptionHandler}
     * methods in {@link RestExceptionHandler})
     *
     * @see #httpStatus
     */
    public <E extends EasException> E status(int status) {
        this.httpStatus = status;
        //noinspection unchecked
        return (E) this;
    }

    /**
     * Set details for this exception. Value must be serializable to JSON.
     *
     * @see #details
     */
    public <E extends EasException> E details(@Nullable Object details) {
        this.details = details;
        //noinspection unchecked
        return (E) this;
    }

    /**
     * Set debug info for this exception. Value must be serializable to JSON.
     *
     * @see #debugInfo
     */
    public <E extends EasException> E debugInfo(@Nullable Object debugInfo) {
        this.debugInfo = debugInfo;
        //noinspection unchecked
        return (E) this;
    }


    /**
     * Returns string representation of this exception in format:
     * <pre>
     *     &lt;exception_name&gt; {status='&lt;http_status&gt;', code='&lt;exception_code&gt;', message='[exception_message]'}
     * </pre>
     */
    @Override
    public String toString() {
        return String.format("%s {status='%d', code='%s', message='%s'}", getClass().getSimpleName(), httpStatus, code, super.getMessage());
    }

    /**
     * Returns exception message including {@code details} and {@code debugInfo}.
     */
    @Beta // message format may be changed in the future
    public String getDetailedMessage() {
        StringBuilder msg = new StringBuilder(super.getMessage());
        if (details != null) {
            msg.append("\n\t\t").append("details=").append(toJsonString(details));
        }
        if (debugInfo != null) {
            msg.append("\n\t\t").append("debugInfo=").append(toJsonString(debugInfo));
        }
        return msg.toString();
    }
}
