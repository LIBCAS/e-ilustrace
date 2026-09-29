package cz.inqool.eas.common.exception;

import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;

/**
 * @deprecated use {@link cz.inqool.eas.common.exception.v2.ForbiddenObject} instead
 */
@Getter
@Deprecated
public class ForbiddenObject extends GeneralException implements CodedException, DetailedException {

    private final Class<?> clazz;
    private String objectId;
    private final Enum<? extends ExceptionCodeEnum<?>> errorCode;
    private Object details;


    public ForbiddenObject(Object object, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.clazz = object.getClass();
        if (object instanceof Domain) {
            this.objectId = ((Domain<?>) object).getId();
        }
        this.errorCode = errorCode;
    }

    public ForbiddenObject(Object object, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this(object, errorCode);
        this.details = details;
    }

    public ForbiddenObject(Class<?> clazz, String objectId, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.clazz = clazz;
        this.objectId = objectId;
        this.errorCode = errorCode;
    }

    public ForbiddenObject(Class<?> clazz, String objectId, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this(clazz, objectId, errorCode);
        this.details = details;
    }


    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "ForbiddenObject{" +
                "class=" + clazz +
                ", objectId='" + objectId + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }


    public enum ErrorCode implements ExceptionCodeEnum<ErrorCode> {
        FORBIDDEN
    }
}
