package cz.inqool.eas.common.security.form.twoFactor;

import cz.inqool.eas.common.exception.CodedException;
import cz.inqool.eas.common.exception.GeneralException;
import lombok.Getter;

/**
 * Exception representing error with {@link TwoFactorSignIn}
 **/
@Getter
public class TwoFactorException extends GeneralException implements CodedException {

    private String userId;
    private Enum<? extends ExceptionCodeEnum<?>> errorCode;

    public TwoFactorException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public TwoFactorException(String userId, ErrorCode errorCode) {
        this.userId = userId;
        this.errorCode = errorCode;
    }

    public enum ErrorCode implements ExceptionCodeEnum<ErrorCode> {
        SIGN_IN_NOT_FOUND,
        SIGN_IN_EXPIRED,
        NUMBER_OF_ATTEMPTS_EXCEEDED,
        USER_MISSING_EMAIL,
        MISSING_PRE_AUTH
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "TwoFactorSignInException{" +
                "userId='" + userId + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }
}
