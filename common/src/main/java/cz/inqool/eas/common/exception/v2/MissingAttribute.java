package cz.inqool.eas.common.exception.v2;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.annotations.Beta;
import cz.inqool.eas.common.exception.v2.MissingAttribute.Details.DetailsBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.FieldNameConstants;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Consumer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static cz.inqool.eas.common.exception.v2.MissingAttribute.Details.Fields.*;

@Beta
public class MissingAttribute extends EasException {

    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.BAD_REQUEST;


    public MissingAttribute(String code) {
        super(code, DEFAULT_HTTP_STATUS.value());
    }

    public MissingAttribute(String code, String message) {
        super(code, DEFAULT_HTTP_STATUS.value(), message);
    }

    public MissingAttribute(String code, Throwable cause) {
        super(code, DEFAULT_HTTP_STATUS.value(), cause);
    }

    public MissingAttribute(String code, String message, Throwable cause) {
        super(code, DEFAULT_HTTP_STATUS.value(), message, cause);
    }


    public MissingAttribute details(Consumer<DetailsBuilder> detailsCustomizer) {
        DetailsBuilder detailsBuilder = Details.builder();
        detailsCustomizer.accept(detailsBuilder);
        return super.details(detailsBuilder.build());
    }

    public MissingAttribute debugInfo(Consumer<DetailsBuilder> detailsCustomizer) {
        DetailsBuilder detailsBuilder = Details.builder();
        detailsCustomizer.accept(detailsBuilder);
        return super.debugInfo(detailsBuilder.build());
    }


    @Getter
    @Builder
    @FieldNameConstants
    @JsonInclude(NON_NULL)
    @JsonPropertyOrder({id, clazz, property, properties})
    public static class Details {

        /**
         * ID of object which caused this exception
         */
        private String id;

        /**
         * Type of object which caused this exception
         */
        @JsonProperty("class")
        private Class<?> clazz;

        /**
         * Source class where this exception was raised
         * <p>
         * Can be used in conjunction with {@link LogStrategy} when no stacktrace is logged but still the information
         * about exception source is needed.
         */
        private Class<?> sourceClass;

        /**
         * Source method (in {@link #sourceClass}) where this exception was raised
         * <p>
         * Can be used in conjunction with {@link LogStrategy} when no stacktrace is logged but still the information
         * about exception source is needed.
         */
        private String sourceMethod;

        /**
         * Name of property that is missing
         */
        private String property;

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
