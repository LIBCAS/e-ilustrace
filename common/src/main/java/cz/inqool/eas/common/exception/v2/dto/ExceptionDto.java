package cz.inqool.eas.common.exception.v2.dto;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.authored.user.UserReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

/**
 * Common exception DTO class specifying atributes contained in all exception responses.
 */
@Beta
@Getter
@FieldNameConstants
public abstract class ExceptionDto {

    /**
     * Date and time of exception raising, mandatory
     */
    @NotNull
    private String timestamp;

    /**
     * Exception code, mandatory
     */
    @NotNull
    private String code;

    /**
     * User who caused the exception to be thrown, optional
     */
    @Setter
    private UserReference user;


    /**
     * Only for deserialization purposes
     */
    protected ExceptionDto() {
    }

    public ExceptionDto(String timestamp, String code) {
        this.timestamp = timestamp;
        this.code = code;
    }
}
