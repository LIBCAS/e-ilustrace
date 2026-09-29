package cz.inqool.eas.common.exception;

import lombok.Getter;

/**
 * Class for throwing exceptions when there is problem with method's argument
 *
 * @deprecated use {@link cz.inqool.eas.common.exception.v2.InvalidArgument} instead
 */
@Getter
@Deprecated
public class InvalidArgument extends GeneralException implements CodedException, DetailedException {

    private final String name;
    private Object value;
    private final Enum<? extends ExceptionCodeEnum<?>> errorCode;
    private Object details;


    public InvalidArgument(String name, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this.name = name;
        this.errorCode = errorCode;
    }

    public InvalidArgument(String name, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this(name, errorCode);
        this.details = details;
    }

    public InvalidArgument(String name, Object value, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this(name, errorCode);
        this.value = value;
    }

    public InvalidArgument(String name, Exception cause, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        super(cause);
        this.name = name;
        this.errorCode = errorCode;
    }

    public InvalidArgument(String name, Object value, Exception cause, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this(name, cause, errorCode);
        this.value = value;
    }


    @Override
    public String toString() {
        return "InvalidArgument{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }

    @Override
    public String getMessage() {
        return toString();
    }


    public enum ErrorCode implements ExceptionCodeEnum<ErrorCode> {
        CANNOT_READ_FILE,
        CONVERSION_TO_TYPE_NOT_SUPPORTED,
        ENCODING_ERROR,
        NOT_XML_FILE,
        NO_SHEET_TYPE_INCLUDED,
        NULL_ARGUMENT,
        PARSING_ERROR,
        SETTING_TYPE_VALUE_MISMATCH,
        SIZE_TOO_BIG,
        TYPE_MISMATCH,
        WRONG_ARGUMENT_SUBCLASS,
        WRONG_FORMAT_ARGUMENT
    }
}
