package cz.inqool.eas.common.exception.v2;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.annotations.Beta;
import cz.inqool.eas.common.exception.v2.InvalidArgument.Details.DetailsBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.FieldNameConstants;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Consumer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static cz.inqool.eas.common.exception.v2.InvalidArgument.Details.Fields.*;

@Beta
public class InvalidArgument extends EasException {

    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.BAD_REQUEST;


    public InvalidArgument(String code) {
        super(code, DEFAULT_HTTP_STATUS.value());
    }

    public InvalidArgument(String code, String message) {
        super(code, DEFAULT_HTTP_STATUS.value(), message);
    }

    public InvalidArgument(String code, Throwable cause) {
        super(code, DEFAULT_HTTP_STATUS.value(), cause);
    }

    public InvalidArgument(String code, String message, Throwable cause) {
        super(code, DEFAULT_HTTP_STATUS.value(), message, cause);
    }


    public InvalidArgument details(Consumer<DetailsBuilder> detailsCustomizer) {
        DetailsBuilder detailsBuilder = Details.builder();
        detailsCustomizer.accept(detailsBuilder);
        return super.details(detailsBuilder.build());
    }

    public InvalidArgument debugInfo(Consumer<DetailsBuilder> detailsCustomizer) {
        DetailsBuilder detailsBuilder = Details.builder();
        detailsCustomizer.accept(detailsBuilder);
        return super.debugInfo(detailsBuilder.build());
    }


    @Getter
    @Builder
    @FieldNameConstants
    @JsonInclude(NON_NULL)
    @JsonPropertyOrder({name, value, properties})
    public static class Details {

        /**
         * Name of invalid argument
         */
        private String name;

        /**
         * Value of invalid argument
         */
        private Object value;

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
