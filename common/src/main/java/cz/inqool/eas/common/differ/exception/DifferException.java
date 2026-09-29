package cz.inqool.eas.common.differ.exception;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.annotations.Beta;
import cz.inqool.eas.common.exception.DetailedException;
import cz.inqool.eas.common.exception.v2.LogStrategy;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.function.Consumer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static cz.inqool.eas.common.differ.exception.DifferException.Details.Fields.*;

@Beta
public class DifferException extends RuntimeException implements DetailedException {

    /**
     * Exception debug details, they're not propagated to client within production environment. Only for debug or
     * logging purposes.
     */
    @Nullable
    protected Object debugInfo;

    public DifferException(String message) {
        super(message);
    }

    public DifferException(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public Object getDetails() {
        return debugInfo;
    }

    /**
     * Set debug info for this exception. Value must be serializable to JSON.
     *
     * @see #debugInfo
     */
    public DifferException debugInfo(@Nullable Object debugInfo) {
        this.debugInfo = debugInfo;
        return this;
    }

    public DifferException debugInfo(Consumer<DifferException.Details.DetailsBuilder> detailsCustomizer) {
        DifferException.Details.DetailsBuilder detailsBuilder = DifferException.Details.builder();
        detailsCustomizer.accept(detailsBuilder);
        return this.debugInfo(detailsBuilder.build());
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
        @JsonAnyGetter
        // to serialize map properties as unwrapped (alternative to @JsonUnwrapped which does not work on maps)
        public Map<String, Object> getProperties() {
            return properties;
        }
    }
}
