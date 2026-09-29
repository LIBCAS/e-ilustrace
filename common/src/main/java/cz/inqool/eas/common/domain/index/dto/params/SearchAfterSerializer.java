package cz.inqool.eas.common.domain.index.dto.params;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.inqool.eas.common.utils.JsonUtils;

import java.io.IOException;
import java.util.Map;

public class SearchAfterSerializer extends JsonSerializer<Object[]> {

    @Override
    public void serialize(Object[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(JsonUtils.toJsonString(value, Map.of()));
    }
}
