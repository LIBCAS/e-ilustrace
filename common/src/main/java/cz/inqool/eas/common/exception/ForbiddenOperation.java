package cz.inqool.eas.common.exception;

import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;

/**
 * @deprecated use {@link cz.inqool.eas.common.exception.v2.ForbiddenOperation} instead
 */
@Getter
@Deprecated
public class ForbiddenOperation extends GeneralException implements CodedException, DetailedException {

    private final Class<?> clazz;
    private String objectId;
    private final Enum<? extends ExceptionCodeEnum<?>> errorCode;
    private Object details;


    public ForbiddenOperation(Object object, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.clazz = object.getClass();
        if (object instanceof Domain) {
            this.objectId = ((Domain<?>) object).getId();
        }
        this.errorCode = errorCode;
    }

    public ForbiddenOperation(Object object, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this(object, errorCode);
        this.details = details;
    }

    public ForbiddenOperation(Class<?> clazz, String objectId, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.clazz = clazz;
        this.objectId = objectId;
        this.errorCode = errorCode;
    }

    public ForbiddenOperation(Class<?> clazz, String objectId, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this(clazz, objectId, errorCode);
        this.details = details;
    }

    public ForbiddenOperation(Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.clazz = null;
        this.objectId = null;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "ForbiddenOperation{" +
                "class=" + clazz +
                ", objectId='" + objectId + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }


    public enum ErrorCode implements ExceptionCodeEnum<ErrorCode> {
        // fixme: clear specific codes
        REPORT_REQUEST_NOT_TO_PROCESS,
        WRONG_STATE,
        NOT_ALLOWED
    }
}
