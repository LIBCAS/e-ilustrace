package cz.inqool.eas.common.template;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.inqool.eas.common.exception.v2.EasException;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.FieldNameConstants;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Consumer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static cz.inqool.eas.common.exception.v2.ExtensionNotAllowedException.Details.Fields.filename;
import static cz.inqool.eas.common.exception.v2.ExtensionNotAllowedException.Details.Fields.properties;

/**
 * Class for throwing exceptions when there was a problem during template processing.
 */
public class TemplateProcessingException extends EasException {

    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public TemplateProcessingException(String code) {
        super(code, DEFAULT_HTTP_STATUS.value());
    }

    public TemplateProcessingException(String code, String message) {
        super(code, DEFAULT_HTTP_STATUS.value(), message);
    }

    public TemplateProcessingException(String code, Throwable cause) {
        super(code, DEFAULT_HTTP_STATUS.value(), cause);
    }

    public TemplateProcessingException(String code, String message, Throwable cause) {
        super(code, DEFAULT_HTTP_STATUS.value(), message, cause);
    }

    public TemplateProcessingException details(Consumer<TemplateProcessingException.Details.DetailsBuilder> detailsCustomizer) {
        TemplateProcessingException.Details.DetailsBuilder detailsBuilder = TemplateProcessingException.Details.builder();
        detailsCustomizer.accept(detailsBuilder);
        return super.details(detailsBuilder.build());
    }

    public TemplateProcessingException debugInfo(Consumer<TemplateProcessingException.Details.DetailsBuilder> detailsCustomizer) {
        TemplateProcessingException.Details.DetailsBuilder detailsBuilder = TemplateProcessingException.Details.builder();
        detailsCustomizer.accept(detailsBuilder);
        return super.debugInfo(detailsBuilder.build());
    }


    @Getter
    @Builder
    @FieldNameConstants
    @JsonInclude(NON_NULL)
    @JsonPropertyOrder({filename, properties})
    public static class Details {

        private String filename;

        @Singular
        private Map<String, Object> properties;


        @JsonAnyGetter
        // to serialize map properties as unwrapped (alternative to @JsonUnwrapped which does not work on maps)
        public Map<String, Object> getProperties() {
            return properties;
        }
    }
}
