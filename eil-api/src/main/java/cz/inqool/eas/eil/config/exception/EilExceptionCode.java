package cz.inqool.eas.eil.config.exception;

import cz.inqool.eas.common.exception.v2.ExceptionCode;

public interface EilExceptionCode extends ExceptionCode {
    String ENTITY_ALREADY_EXISTS = "ENTITY_ALREADY_EXISTS";
    String UNRECOGNIZED_LINK_DESCRIPTION = "UNRECOGNIZED_LINK_DESCRIPTION";
    String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    String ACCOUNT_NOT_VALIDATED = "ACCOUNT_NOT_VALIDATED";
    String ICONCLASS_API_REQUEST_FAILED = "ICONCLASS_API_REQUEST_FAILED";
    String MALFORMED_URL = "MALFORMED_URL";
    String ICONCLASS_URL_DOES_NOT_EXIST = "ICONCLASS_URL_DOES_NOT_EXIST";
}
