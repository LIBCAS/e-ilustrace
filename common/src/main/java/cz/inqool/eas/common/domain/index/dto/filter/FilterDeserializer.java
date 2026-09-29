package cz.inqool.eas.common.domain.index.dto.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidNullException;

import java.io.IOException;

/**
 * Custom deserializer for classes implementing {@link Filter} interface. Uses {@code operation} property to choose
 * proper class to deserialize to.
 */
public class FilterDeserializer extends StdDeserializer<Filter> {

    protected FilterDeserializer() {
        super(Filter.class);
    }


    @Override
    public Filter deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode node = p.readValueAsTree();

        // Select the concrete class based on the existence of a property
        if (node.get("operation") != null) {
            return p.getCodec().treeToValue(node, AbstractFilter.class);
        } else {
            throw InvalidNullException.from(p, Filter.class, "Required property 'operation' is missing."); // InternalFilter is not allowed, can be providen only on back-end
//            return p.getCodec().treeToValue(node, InternalFilter.class); // uncomment to allow deserialization of InternalFilter
        }
    }
}
