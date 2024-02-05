package cz.inqool.eas.common.reporting.convert;

import com.fasterxml.jackson.databind.JavaType;
import cz.inqool.eas.common.db.JsonConverter;

import javax.persistence.Converter;
import java.util.Map;

/**
 * Convert serialized report input.
 */
@Converter
public class ReportInputConverter extends JsonConverter<Map<String, Object>> {

    @Override
    public JavaType getType() {
        return getObjectMapper().getTypeFactory().constructMapType(Map.class, String.class, Object.class);
    }
}
