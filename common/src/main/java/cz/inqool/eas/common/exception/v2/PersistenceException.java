package cz.inqool.eas.common.exception.v2;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.annotations.Beta;
import cz.inqool.eas.common.exception.v2.PersistenceException.Details.DetailsBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.FieldNameConstants;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Consumer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static cz.inqool.eas.common.exception.v2.PersistenceException.Details.Fields.properties;

@Beta
public class PersistenceException extends EasException {

    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.FORBIDDEN;


    public PersistenceException(String code) {
        super(code, DEFAULT_HTTP_STATUS.value());
    }

    public PersistenceException(String code, String message) {
        super(code, DEFAULT_HTTP_STATUS.value(), message);
    }

    public PersistenceException(String code, Throwable cause) {
        super(code, DEFAULT_HTTP_STATUS.value(), cause);
    }

    public PersistenceException(String code, String message, Throwable cause) {
        super(code, DEFAULT_HTTP_STATUS.value(), message, cause);
    }


    public PersistenceException details(Consumer<DetailsBuilder> detailsCustomizer) {
        DetailsBuilder detailsBuilder = Details.builder();
        detailsCustomizer.accept(detailsBuilder);
        return super.details(detailsBuilder.build());
    }

    public PersistenceException debugInfo(Consumer<DetailsBuilder> detailsCustomizer) {
        DetailsBuilder detailsBuilder = Details.builder();
        detailsCustomizer.accept(detailsBuilder);
        return super.debugInfo(detailsBuilder.build());
    }


    @Getter
    @Builder
    @FieldNameConstants
    @JsonInclude(NON_NULL)
    @JsonPropertyOrder({properties})
    public static class Details {

        /**
         * Additional custom properties.
         */
        @Singular
        private Map<String, Object> properties;


        @JsonInclude(ALWAYS)
        @JsonAnyGetter // to serialize map properties as unwrapped (alternative to @JsonUnwrapped which does not work on maps)
        public Map<String, Object> getProperties() {
            return properties;
        }
    }
}
