package cz.inqool.eas.common.exception;

import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;

/**
 * @deprecated use {@link cz.inqool.eas.common.exception.v2.MissingAttribute} instead
 */
@Getter
@Deprecated
public class MissingAttribute extends GeneralException implements CodedException, DetailedException {

    private final Class<?> clazz;
    private String objectId;
    private final String attribute;

    private final Enum<? extends ExceptionCodeEnum<?>> errorCode;
    private Object details;


    public MissingAttribute(Object object, String attribute, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.clazz = object.getClass();
        if (object instanceof Domain) {
            this.objectId = ((Domain<?>) object).getId();
        }
        this.attribute = attribute;
        this.errorCode = errorCode;
    }

    public MissingAttribute(Object object, String attribute, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this(object, attribute, errorCode);
        this.details = details;
    }

    public MissingAttribute(Class<?> clazz, String objectId, String attribute, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.clazz = clazz;
        this.objectId = objectId;
        this.attribute = attribute;
        this.errorCode = errorCode;
    }

    public MissingAttribute(Class<?> clazz, String objectId, String attribute, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this(clazz, objectId, attribute, errorCode);
        this.details = details;
    }


    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "MissingAttribute{" +
                "class=" + clazz +
                ", objectId='" + objectId + '\'' +
                ", attribute='" + attribute + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }


    public enum ErrorCode implements ExceptionCodeEnum<ErrorCode> {
        FIELD_IS_NULL,
        MISSING_REQUEST_BODY,
        MISSING_SOAP_AUTH_HEADER
    }
}
