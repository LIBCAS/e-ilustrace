package cz.inqool.eas.common.exception.v2.rest.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.inqool.eas.common.exception.v2.dto.ExceptionDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

/**
 * REST exception DTO class specifying atributes contained in REST responses.
 */
@Getter
@FieldNameConstants
@JsonPropertyOrder({
        ExceptionDto.Fields.timestamp,
        RestExceptionDto.Fields.status,
        ExceptionDto.Fields.code,
        RestExceptionDto.Fields.path,
        ExceptionDto.Fields.user,
        RestExceptionDto.Fields.message,
        RestExceptionDto.Fields.details,
        RestExceptionDto.Fields.debugInfo
})
public class RestExceptionDto extends ExceptionDto {

    /**
     * HTTP status value, mandatory
     */
    @NotNull
    private int status;

    /**
     * Request URL path with HTTP method prefix, mandatory
     */
    @NotNull
    private String path;

    /**
     * Exception message, optional
     */
    @Setter
    private String message;

    /**
     * Formatted exception details, optional
     */
    @Setter
    private Object details;

    /**
     * Debug information, optional
     */
    @Setter
    private Object debugInfo;


    /**
     * Only for deserialization purposes
     */
    private RestExceptionDto() {
    }

    public RestExceptionDto(String timestamp, String code, int status, String path) {
        super(timestamp, code);
        this.status = status;
        this.path = path;
    }
}
