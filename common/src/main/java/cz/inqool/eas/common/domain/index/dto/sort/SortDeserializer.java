package cz.inqool.eas.common.domain.index.dto.sort;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidNullException;

import java.io.IOException;

/**
 * Custom deserializer for classes implementing {@link Sort} interface. Uses {@code type} property to choose proper
 * class to deserialize to.
 */
public class SortDeserializer extends StdDeserializer<Sort<?>> {

    protected SortDeserializer() {
        super(Sort.class);
    }


    @Override
    public Sort<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode node = p.readValueAsTree();

        // Select the concrete class based on the existence of a property
        if (node.get("type") != null) {
            return p.getCodec().treeToValue(node, AbstractSort.class);
        } else {
            return p.getCodec().treeToValue(node, FieldSort.class); // todo replace with below when defaultImpl won't be supported - see cz.inqool.eas.common.domain.index.dto.sort.AbstractSort
//            throw InvalidNullException.from(p, Sort.class, "Required property 'type' is missing.");
        }
    }
}
