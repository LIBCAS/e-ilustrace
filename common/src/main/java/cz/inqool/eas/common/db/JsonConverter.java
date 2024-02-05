package cz.inqool.eas.common.db;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;

import javax.persistence.AttributeConverter;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static cz.inqool.eas.common.utils.JsonUtils.fromJsonString;
import static cz.inqool.eas.common.utils.JsonUtils.toJsonString;

/**
 * Converter for database columns containing JSON-serialized objects.
 */
public abstract class JsonConverter<X> implements AttributeConverter<X, String> {

    private ObjectMapper objectMapper;
    private JavaType javaType;


    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToDatabaseColumn(X attribute) {
        if (attribute == null) {
            return null;
        }

        return toJsonString(attribute, getObjectMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return fromJsonString(dbData, getJavaType(), getObjectMapper());
    }

    protected ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            configureObjectMapper(objectMapper);
        }

        return objectMapper;
    }

    /**
     * Override in subclasses to configure object mapper
     */
    protected void configureObjectMapper(@NonNull ObjectMapper objectMapper) {
        objectMapper.disable(INDENT_OUTPUT);
    }

    private JavaType getJavaType() {
        if (javaType == null) {
            javaType = getType();
        }

        return javaType;
    }

    /**
     * Return the type of serialized object.
     */
    public abstract JavaType getType();
}
