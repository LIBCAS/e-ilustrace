package cz.inqool.eas.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.inqool.eas.common.exception.CodedException;
import cz.inqool.eas.common.exception.DetailedException;
import cz.inqool.eas.common.exception.GeneralException;
import cz.inqool.eas.common.exception.v2.EasException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

import static cz.inqool.eas.common.exception.ExceptionUtils.getLastCause;
import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;
import static cz.inqool.eas.common.utils.JsonUtils.toJsonString;

/**
 * Abstract class serving as a base for exception handling objects.
 */
@Getter
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseException {

    @JsonIgnore
    protected Throwable throwable;

    protected String timestamp;
    protected String exception;
    protected String message;
    protected String cause;
    protected String code;
    protected Object details;


    protected BaseException(Throwable throwable) {
        this(throwable, throwable.getMessage());
    }

    protected BaseException(Throwable throwable, String message) {
        this.throwable = throwable;
        this.exception = throwable.getClass().toString();
        this.timestamp = Instant.now().toString();
        this.message = message;
        this.cause = getCause(throwable);
        if (throwable instanceof CodedException) {
            CodedException codedException = (CodedException) throwable;
            ifPresent(codedException.getErrorCode(), codeEnum -> this.code = codeEnum.name());
        }
        if (throwable instanceof DetailedException) {
            this.details = ((DetailedException) throwable).getDetails();
        }
    }

    protected BaseException(EasException throwable, String message) {
        this.throwable = throwable;
        this.exception = throwable.getClass().toString();
        this.timestamp = Instant.now().toString();
        this.message = message;
        this.cause = getCause(throwable);
        this.code = throwable.getCode();

        if (throwable.getDetails() != null) {
            this.details = throwable.getDetails();
        } else if (throwable.getDebugInfo() != null) {
            this.details = throwable.getDebugInfo();
        }
    }


    protected ObfuscatedException toObfuscatedException() {
        return new ObfuscatedException(throwable.getClass().getName(), message, null);
    }

    public static String getCause(Throwable throwable) {
        Throwable cause = getLastCause(throwable);
        if (cause != throwable) {
            if (cause instanceof GeneralException) {
                return cause.toString();
            } else {
                return cause.getClass().getName() + ": " + cause.getMessage();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return toJsonString(this, true);
    }
}
