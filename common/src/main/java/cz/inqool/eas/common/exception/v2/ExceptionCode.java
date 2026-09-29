package cz.inqool.eas.common.exception.v2;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * List of string constants used as exception codes in {@link EasException#code}.
 * <p>
 * Projects can extend this interface to add own project-specific codes and access all of them within same class.
 */
public interface ExceptionCode {

    /**
     * Required value is {@code null}
     */
    String ARGUMENT_VALUE_IS_NULL = "ARGUMENT_VALUE_IS_NULL";

    /**
     * Forbidden value is not {@code null}
     */
    String ARGUMENT_VALUE_NOT_NULL = "ARGUMENT_VALUE_NOT_NULL";

    /**
     * Required value is {@code null}
     */
    String ATTRIBUTE_VALUE_IS_NULL = "ATTRIBUTE_VALUE_IS_NULL";

    /**
     * Forbidden value is not {@code null}
     */
    String ATTRIBUTE_VALUE_NOT_NULL = "ATTRIBUTE_VALUE_NOT_NULL";

    /**
     * Deleted entities cannot be updated
     */
    String DELETED_ENTITY_NOT_UPDATABLE = "DELETED_ENTITY_NOT_UPDATABLE";

    /**
     * Access to given endpoint is denied (either {@link PreAuthorize} or other Spring security mechanisms prevented
     * access)
     */
    String ENDPOINT_ACCESS_DENIED = "ENDPOINT_ACCESS_DENIED";

    /**
     * Requested endpoint is not mapped.
     */
    String ENDPOINT_NOT_FOUND = "ENDPOINT_NOT_FOUND";

    /**
     * Entity was not found
     */
    String ENTITY_NOT_EXIST = "ENTITY_NOT_EXIST";

    /**
     * Wrong state of export request
     */
    String EXPORT_REQUEST_WRONG_STATE = "EXPORT_REQUEST_WRONG_STATE";

    /**
     * File is already stored permanently
     */
    String FILE_ALREADY_PERMANENTLY_STORED = "FILE_ALREADY_PERMANENTLY_STORED";

    /**
     * Wrong type of index field used.
     */
    String FIELD_INVALID_TYPE = "FIELD_INVALID_TYPE";

    /**
     * Field is not defined as a full-text field in mapping. Check {@link Field#type()} on field mapping in appropriate index object class
     */
    String FIELD_NOT_FULLTEXT = "FIELD_NOT_FULLTEXT";

    /**
     * Field is not indexed in ElasticSearch index mapping. Check {@link Field#index} on field mapping in appropriate index object class.
     */
    String FIELD_NOT_INDEXED = "FIELD_NOT_INDEXED";

    /**
     * Field is not present in ElasticSearch index mapping. Check appropriate index object class.
     */
    String FIELD_NOT_MAPPED = "FIELD_NOT_MAPPED";

    /**
     * Sorting on a field that has fielddata disabled.
     */
    String FIELDDATA_DISABLED = "FIELDDATA_DISABLED";

    /**
     * An error occurs during index query execution
     */
    String INDEX_QUERY_EXECUTION_ERROR = "INDEX_QUERY_EXECUTION_ERROR";

    /**
     * Access to object is denied
     */
    String OBJECT_ACCESS_DENIED = "OBJECT_ACCESS_DENIED";

    /**
     * Access to operation is denied
     */
    String OPERATION_ACCESS_DENIED = "OPERATION_ACCESS_DENIED";

    /**
     * Required value is {@code null}
     */
    String OBJECT_VALUE_IS_NULL = "OBJECT_VALUE_IS_NULL";

    /**
     * SAML user is not registered in system (token identifier was not recognized)
     */
    String SAML_USER_NOT_REGISTERED = "SAML_USER_NOT_REGISTERED";

    /**
     * Sign request has already been processed
     */
    String SIGN_REQUEST_ALREADY_PROCESSED = "SIGN_REQUEST_ALREADY_PROCESSED";

    /**
     * Size of uploaded file exceeded max size limit
     */
    String SIZE_TOO_BIG = "SIZE_TOO_BIG";

    /**
     * Error during input data validation
     */
    String VALIDATION_FAILED = "VALIDATION_FAILED";

    /**
     * File extension is not supported
     */
    String UNSUPPORTED_FILE_EXTENSION = "UNSUPPORTED_FILE_EXTENSION";
}
