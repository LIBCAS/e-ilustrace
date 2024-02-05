package cz.inqool.eas.common.domain.index.dto.params;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import cz.inqool.eas.common.utils.JsonUtils;

import java.io.IOException;

public class SearchAfterDeserializer extends JsonDeserializer<Object[]> {

    private JavaType javaType;


    @Override
    public Object[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return JsonUtils.fromJsonString(p.getText(), getJavaType());
    }

    private JavaType getJavaType() {
        if (javaType == null) {
            javaType = JsonUtils.typeFactory().constructType(Object[].class);
        }

        return javaType;
    }
}
