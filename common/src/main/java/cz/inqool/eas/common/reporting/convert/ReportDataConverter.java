package cz.inqool.eas.common.reporting.convert;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import cz.inqool.eas.common.db.JsonConverter;

import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Convert serialized report data.
 */
@Converter
public class ReportDataConverter extends JsonConverter<List<Map<String, Object>>> {

    @Override
    public JavaType getType() {
        TypeFactory typeFactory = getObjectMapper().getTypeFactory();

        MapType itemType = typeFactory.constructMapType(Map.class, String.class, Object.class);
        return typeFactory.constructCollectionType(ArrayList.class, itemType);
    }
}
