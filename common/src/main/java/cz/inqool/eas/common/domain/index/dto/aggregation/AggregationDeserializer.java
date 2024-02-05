package cz.inqool.eas.common.domain.index.dto.aggregation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;

import java.io.IOException;

/**
 * Custom deserializer for classes implementing {@link Aggregation} interface. Uses {@code family} property to choose
 * proper class to deserialize to.
 */
public class AggregationDeserializer extends StdDeserializer<Aggregation> {

    protected AggregationDeserializer() {
        super(Filter.class);
    }


    @Override
    public Aggregation deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode node = p.readValueAsTree();

        // Select the concrete class based on the existence of a property
        JsonNode family = (JsonNode) node.get("family");
        if (family != null && family.isValueNode()) {
            Class<? extends Aggregation> aggregationClass;
            switch (family.asText()) {
                case "BUCKET":
                    aggregationClass = BucketAggregation.class;
                    break;
                case "METRIC":
                    aggregationClass = MetricAggregation.class;
                    break;
                default:
                    aggregationClass = AbstractAggregation.class;
            }
            return p.getCodec().treeToValue(node, aggregationClass);
        } else {
            throw InvalidNullException.from(p, Filter.class, "Required property 'family' is missing.");
        }
    }
}
