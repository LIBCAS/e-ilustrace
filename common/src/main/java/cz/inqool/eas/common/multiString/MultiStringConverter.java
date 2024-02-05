package cz.inqool.eas.common.multiString;

import com.fasterxml.jackson.databind.JavaType;
import cz.inqool.eas.common.db.JsonConverter;

import javax.persistence.Converter;

/**
 * JPA converter for {@link MultiString}
 */
@Converter(autoApply = true)
public class MultiStringConverter extends JsonConverter<MultiString> {

    @Override
    public JavaType getType() {
        return getObjectMapper().constructType(MultiString.class);
    }

    @Override
    public MultiString convertToEntityAttribute(String dbData) {
        try {
            return super.convertToEntityAttribute(dbData);
        } catch (Exception ignored) {
            // ignore
            return null;
        }
    }
}
