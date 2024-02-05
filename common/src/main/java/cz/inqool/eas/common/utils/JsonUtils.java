package cz.inqool.eas.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.factories.WrapperFactory;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.exception.ExceptionUtils.checked;
import static java.lang.String.format;

/**
 * Utility methods for working with JSON.
 */
public class JsonUtils {

    private static ObjectMapper defaultObjectMapper;
    private static JsonSchemaGenerator jsonSchemaGenerator;


    /**
     * Converts object to JSON string using default {@link ObjectMapper}.
     *
     * @param o Object to convert
     * @return JSON string
     */
    public static String toJsonString(Object o) {
        return toJsonString(o, false);
    }

    /**
     * Converts object to JSON string using default {@link ObjectMapper}.
     *
     * @param o           Object to convert
     * @param prettyPrint Pretty print the output
     * @return JSON string
     */
    public static String toJsonString(Object o, boolean prettyPrint) {
        return toJsonString(o, Map.of(SerializationFeature.INDENT_OUTPUT, prettyPrint));
    }

    /**
     * Converts object to JSON string using default {@link ObjectMapper}.
     *
     * @param o      object to convert
     * @param config custom conversion configuration
     * @return JSON string
     */
    public static String toJsonString(@Nullable Object o, @NonNull Map<SerializationFeature, Boolean> config) {
        ensureObjectMapperInitialized();

        Map<SerializationFeature, Boolean> previousConfig = config.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), feature -> defaultObjectMapper.isEnabled(feature)));
        config.forEach((key, value) -> defaultObjectMapper.configure(key, value));

        try {
            return toJsonString(o, defaultObjectMapper);
        } finally {
            previousConfig.forEach((key, value) -> defaultObjectMapper.configure(key, value));
        }
    }

    /**
     * Converts object to JSON string using custom {@link ObjectMapper}.
     *
     * @param o                  object to convert
     * @param customObjectMapper custom object mapper used for conversion
     * @return JSON string
     */
    public static String toJsonString(@Nullable Object o, @NonNull ObjectMapper customObjectMapper) {
        try {
            return customObjectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(format("Failed when serializing %s to JSON string", (o != null) ? o.getClass() : null), e);
        }
    }

    /**
     * Converts JSON string to object using specified class.
     *
     * @param json JSON string
     * @param type Class to use
     * @param <T>  Type of object
     * @return Object of provided type
     */
    public static <T> T fromJsonString(@NotNull String json, @NotNull Class<T> type) {
        return fromJsonString(json, typeFactory().constructType(type));
    }

    /**
     * Converts JSON string to object using specified type reference.
     *
     * @param json JSON string
     * @param type Type reference to use
     * @param <T>  Type of object
     * @return Object of provided type
     */
    public static <T> T fromJsonString(@NotNull String json, @NotNull TypeReference<T> type) {
        return fromJsonString(json, typeFactory().constructType(type));
    }

    /**
     * Converts JSON string to object using specified java type.
     *
     * @param json JSON string
     * @param type Java type to use
     * @param <T>  Type of object
     * @return Object of provided type
     */
    public static <T> T fromJsonString(@NotNull String json, @NotNull JavaType type) {
        return fromJsonString(json, type, Map.of());
    }

    /**
     * Converts JSON string to object using specified java type.
     *
     * @param json   JSON string
     * @param type   Java type to use
     * @param config custom conversion configuration
     * @param <T>    Type of object
     * @return Object of provided type
     */
    public static <T> T fromJsonString(@NotNull String json, @NotNull JavaType type, @NonNull Map<DeserializationFeature, Boolean> config) {
        ensureObjectMapperInitialized();

        Map<DeserializationFeature, Boolean> previousConfig = config.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), feature -> defaultObjectMapper.isEnabled(feature)));
        config.forEach((key, value) -> defaultObjectMapper.configure(key, value));

        try {
            return fromJsonString(json, type, defaultObjectMapper);
        } finally {
            previousConfig.forEach((key, value) -> defaultObjectMapper.configure(key, value));
        }
    }

    /**
     * Converts JSON string to object using specified java type.
     *
     * @param json               JSON string
     * @param type               Java type to use
     * @param customObjectMapper custom object mapper used for conversion
     * @param <T>                Type of object
     * @return Object of provided type
     */
    public static <T> T fromJsonString(@NotNull String json, @NotNull JavaType type, @NonNull ObjectMapper customObjectMapper) {
        try {
            return customObjectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(format("Failed to read JSON value as %s", type.getTypeName()), e);
        }
    }

    public static <T> T fromJsonStringParametrized(@NotNull String json, @NotNull Class<T> parametrized, Class<?>... parameterTypes) {
        return fromJsonString(json, typeFactory().constructParametricType(parametrized, parameterTypes));
    }

    /**
     * Converts JSON stream to object using specified class.
     *
     * @param json JSON stream
     * @param type Class to use
     * @param <T>  Type of object
     * @return Object of provided type
     */
    public static <T> T fromJsonStream(@NotNull InputStream json, @NotNull Class<T> type) {
        return fromJsonStream(json, typeFactory().constructType(type));
    }

    /**
     * Converts JSON stream to object using specified type reference.
     *
     * @param json JSON stream
     * @param type Type reference to use
     * @param <T>  Type of object
     * @return Object of provided type
     */
    public static <T> T fromJsonStream(@NotNull InputStream json, @NotNull TypeReference<T> type) {
        return fromJsonStream(json, typeFactory().constructType(type));
    }

    public static <T> T fromJsonStream(@NotNull InputStream json, @NotNull JavaType type) {
        ensureObjectMapperInitialized();

        try {
            return defaultObjectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to read JSON value as %s", type.getTypeName()), e);
        }
    }

    public static <IN, OUT> OUT convert(IN input, @NotNull Class<OUT> outputType) {
        ensureObjectMapperInitialized();

        try {
            return defaultObjectMapper.convertValue(input, outputType);
        } catch (Exception e) {
            throw new RuntimeException(format("Failed to convert %s to %s", (input != null) ? input.getClass() : null, outputType), e);
        }
    }

    public static <IN, OUT> OUT convert(IN input, @NotNull Class<OUT> outputType, Class<?>... parameterTypes) {
        ensureObjectMapperInitialized();

        try {
            JavaType parametricType = defaultObjectMapper.getTypeFactory().constructParametricType(outputType, parameterTypes);
            return defaultObjectMapper.convertValue(input, parametricType);
        } catch (Exception e) {
            throw new RuntimeException(format("Failed to convert %s to %s", (input != null) ? input.getClass() : null, outputType), e);
        }
    }

    public static TypeFactory typeFactory() {
        ensureObjectMapperInitialized();
        return defaultObjectMapper.getTypeFactory();
    }

    /**
     * Creates a JSON schema for given class
     */
    public static JsonSchema createJsonSchema(Class<?> clazz) {
        ensureJsonSchemaGeneratorInitialized();

        return checked(() -> jsonSchemaGenerator.generateSchema(clazz));
    }

    private static void ensureObjectMapperInitialized() {
        if (defaultObjectMapper == null) {
            defaultObjectMapper = newObjectMapper();
        }
    }

    private static void ensureJsonSchemaGeneratorInitialized() {
        if (jsonSchemaGenerator == null) {
            ensureObjectMapperInitialized();

            VisitorContext visitorContext = new VisitorContext() {
                @Override
                public String addSeenSchemaUri(JavaType aSeenSchema) {
                    return null; // not to send property "id" (such as 'urn:jsonschema:cz:inqool:peva:system:notification:template:model:DelimitationRequest')
                }

                @Override
                public String getSeenSchemaUri(JavaType aSeenSchema) {
                    return null; // to not include $ref attribute on repeated models and unwind all model attributes
                }
            };

            jsonSchemaGenerator = new JsonSchemaGenerator(defaultObjectMapper, new WrapperFactory().getWrapper(null, visitorContext));
        }
    }

    /**
     * Create a new instance of ObjectMapper using same strategy as in autoconfiguration to be able to have multiple
     * instances with different configurations at the same time (Spring Boot treates them as singletons, configuration
     * change will affect whole application)
     *
     * @see JacksonAutoConfiguration.JacksonObjectMapperConfiguration#jacksonObjectMapper
     */
    @SuppressWarnings("JavadocReference")
    public static ObjectMapper newObjectMapper() {
        ApplicationContext applicationContext = ApplicationContextUtils.getApplicationContext();
        if (applicationContext != null) {
            Jackson2ObjectMapperBuilder objectMapperBuilder = applicationContext.getBean(Jackson2ObjectMapperBuilder.class);
            return objectMapperBuilder.createXmlMapper(false).build();
        } else {
            throw new RuntimeException("Application not properly initialized yet.");
        }
    }
}
