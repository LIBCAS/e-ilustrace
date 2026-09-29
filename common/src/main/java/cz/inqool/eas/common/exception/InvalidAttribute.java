package cz.inqool.eas.common.exception;

import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * Class for throwing exceptions when there is problem with object's attribute
 *
 * @deprecated use {@link cz.inqool.eas.common.exception.v2.InvalidAttribute} instead
 */
@Getter
@Deprecated
//fixme matus: @SoapFault(faultCode = FaultCode.SERVER)
public class InvalidAttribute extends GeneralException implements CodedException, DetailedException {

    private final Class<?> clazz;
    private String objectId;
    private final String attribute;
    private final Enum<? extends ExceptionCodeEnum<?>> errorCode;
    private final Object details;


    public InvalidAttribute(Class<?> clazz, String objectId, String attribute, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this(clazz, objectId, attribute, errorCode, null);
    }

    public InvalidAttribute(Class<?> clazz, String objectId, String attribute, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this.clazz = clazz;
        this.objectId = objectId;
        this.attribute = attribute;
        this.errorCode = errorCode;
        this.details = details;
    }

    public InvalidAttribute(Object object, String attribute, Enum<? extends ExceptionCodeEnum<?>> errorCode) {
        this(object, attribute, errorCode, null);
    }

    public InvalidAttribute(Object object, String attribute, Enum<? extends ExceptionCodeEnum<?>> errorCode, Object details) {
        this.clazz = object.getClass();
        if (object instanceof Domain) {
            this.objectId = ((Domain<?>) object).getId();
        }
        this.attribute = attribute;
        this.errorCode = errorCode;
        this.details = details;
    }


    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "InvalidAttribute{" +
                "class=" + clazz +
                ", objectId='" + objectId + '\'' +
                ", attribute='" + attribute + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }


    public enum ErrorCode implements ExceptionCodeEnum<ErrorCode> {
        ALL_NOT_FOUND_QUANTITY_NOT_ALLOWED,
        CHANGE_ALREADY_COMMITTED,
        CLASS_NOT_AN_ENTITY,
        DEPARTMENT_INSTITUTION_CONFLICT,
        DEPARTMENT_UPDATE_FORBIDDEN,
        ENTITY_DRAFT_ALREADY_EXIST,
        EVIDENCE_UNIT_COUNTERPART_NOT_FOUND,
        EVIDENCE_UNIT_DUPLICATE,
        EVIDENCE_UNIT_INVALID_AMOUNT,
        EVIDENCE_UNIT_TYPE_NOT_UNIQUE_IDENTIFIER,
        /** Field is not defined as a full-text field in mapping. Check {@link Field#type()} on field mapping in appropriate index object class */
        FIELD_NOT_FULLTEXT,
        /** Field index mapping has disabled indexing. Check {@link Field#index} on field mapping in appropriate index object class. */
        FIELD_NOT_INDEXED,
        /** Wrong type of ElasticSearch mapping field. */
        FIELD_INVALID_TYPE,
        /** Sorting on a field that has fielddata disabled. */
        FIELDDATA_DISABLED,
        /** Field is not present in ElasticSearch index mapping. Check appropriate index object class. */
        FIELD_NOT_MAPPED,
        INVALID_AMOUNT,
        INVALID_CHANGE_COMMITTED,
        INVALID_CHANGE_INCREASE_TYPE,
        INVALID_CHANGE_LENGTH,
        INVALID_CHANGE_ORIGINATORS,
        INVALID_CHANGE_ORIGINS,
        INVALID_CHANGE_PENDING_TIME,
        INVALID_CHANGE_TIME_RANGE,
        INVALID_CHANGE_TYPE,
        INVALID_EVIDENCE_STATUS,
        INVALID_EXISTING_EVIDENCE_UNITS,
        INVALID_FILE_FORMAT,
        INVALID_MIRROR_CHANGE_CHANGE_TYPE,
        INVALID_MIRROR_CHANGE_INSTITUTION,
        INVALID_NAD_SHEET_LENGTH,
        INVALID_NAD_SHEET_TO_BE_CREATED,
        INVALID_RELATED_CHANGE_TYPE,
        INVALID_SEQUENCE_FORMAT,
        INVALID_STATE,
        INVALID_TARGET_INSTITUTION,
        INVALID_TYPE_ATTRIBUTE,
        MAX_POSSIBLE_QUANTITY_EXCEEDED,
        MIRROR_CHANGE_NOT_NULL,
        NAD_SHEET_CREATED,
        NAD_SHEET_NOT_CREATED,
        NOT_FOUND_QUANTITY_OVERFLOW,
        ONLY_ONE_MAPPING_CLASS_ALLOWED,
        ORIGINATOR_NOT_ON_TARGET,
        PART_CHANGE_REMOVING_ALL_EVIDENCE_UNITS,
        PREFERRED_NAME_NOT_UNIQUE,
        ROLE_ALREADY_EXIST,
        SEQUENCE_NUMBER_NOT_UNIQUE,
        TYPE_NOT_SUPPORTED,
        UNKNOWN_EXTERNAL_CHANGE_TYPE_BASE,
        USERNAME_ALREADY_EXIST
    }
}
