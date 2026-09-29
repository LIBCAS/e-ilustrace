package cz.inqool.eas.common.exception;

import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;

/**
 * @deprecated use {@link cz.inqool.eas.common.exception.v2.ConflictObject} instead
 */
@Getter
@Deprecated
public class ConflictObject extends GeneralException implements CodedException, DetailedException {

    private final Class<?> clazz;
    private String id;
    private Enum<? extends ExceptionCodeEnum<?>> errorCode;
    private Object details;


    public ConflictObject(Class<?> clazz, String id) {
        this(clazz, id, null);
    }

    public ConflictObject(Class<?> clazz, String id, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this(clazz, id, errorCode, null);
    }

    public ConflictObject(Class<?> clazz, String id, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this.clazz = clazz;
        this.id = id;
        this.errorCode = errorCode;
        this.details = details;
    }

    public ConflictObject(Object object) {
        this(object, null);
    }

    public ConflictObject(Object object, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this(object, errorCode, null);
    }

    public ConflictObject(Object object, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this.clazz = object.getClass();
        if (object instanceof Domain) {
            this.id = ((Domain<?>) object).getId();
        }
        this.errorCode = errorCode;
        this.details = details;
    }


    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "ConflictObject{" +
                "clazz=" + clazz +
                ", id='" + id + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }
}
