package cz.inqool.eas.eil.exception;

import cz.inqool.eas.common.exception.v2.ExceptionCode;

public class EilExceptionCode implements ExceptionCode {

    //400
    public static final String WRONG_ARGUMENT_VALUE = "WRONG_ARGUMENT_VALUE";

    //403
    public static final String MISSING_PERMISSION_FOR_OPERATION = "MISSING_PERMISSION_FOR_OPERATION";
    public static final String OBJECT_NOT_IN_STATE_FOR_THIS_OPERATION = "OBJECT_NOT_IN_STATE_FOR_THIS_OPERATION";
    public static final String WRONG_PASSWORD = "WRONG_PASSWORD";
}
