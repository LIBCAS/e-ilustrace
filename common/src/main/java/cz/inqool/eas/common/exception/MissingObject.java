package cz.inqool.eas.common.exception;

import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;

/**
 * @deprecated use {@link cz.inqool.eas.common.exception.v2.MissingObject} instead
 */
@Getter
@Deprecated
public class MissingObject extends GeneralException implements CodedException {

    private final Class<?> clazz;
    private String idProperty;
    private String id;
    private Enum<? extends ExceptionCodeEnum<?>> errorCode;


    public MissingObject(Class<?> clazz, String id) {
        this(clazz, id, (Enum<? extends ExceptionCodeEnum<?>>) null);
    }

    public MissingObject(Class<?> clazz, String id, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this(clazz, "id", id, errorCode);
    }

    public MissingObject(Class<?> clazz, String idProperty, String id) {
        this(clazz, idProperty, id, null);
    }

    public MissingObject(Class<?> clazz, String idProperty, String id, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.clazz = clazz;
        this.idProperty = idProperty;
        this.id = id;
        this.errorCode = errorCode;
    }

    public MissingObject(Object object) {
        this(object, null);
    }

    public MissingObject(Object object, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.clazz = object.getClass();
        if (object instanceof Domain) {
            this.idProperty = "id";
            this.id = ((Domain<?>) object).getId();
        }
        this.errorCode = errorCode;
    }


    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "MissingObject{" +
                "clazz=" + clazz +
                ", idProperty='" + idProperty + '\'' +
                ", id='" + id + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }


    public enum ErrorCode implements ExceptionCodeEnum<ErrorCode> {
        ELECTRONIC_ARCHIVAL_AID_WAS_REMOVED,
        FILE_WAS_REMOVED,
        NO_MATCHING_ITEM
    }
}
