package cz.inqool.eas.common.domain.index.dto.aggregation;

import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.AggregatorFactories;

import java.util.List;

/**
 * A special single bucket aggregation that enables aggregating nested documents.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-nested-aggregation.html">Nested Aggregation</a>
 */
@Schema(
        description = "Nested aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Nested Aggregation",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-nested-aggregation.html"
        )
)
@Getter
@Setter
public class NestedAggregation extends BucketAggregation {

    private String name;

    private String path;


    public NestedAggregation() {
        super(BucketAggregator.NESTED);
    }

    @Builder
    public NestedAggregation(String name, String path, List<Aggregation> aggregations) {
        super(BucketAggregator.NESTED, aggregations);
        this.name = name;
        this.path = path;
    }


    @Override
    public AggregationBuilder toAggregationBuilder(IndexObjectFields indexObjectFields) {
        org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder aggregationBuilder =
                AggregationBuilders.nested(name, path);
        if (aggregations != null && !aggregations.isEmpty()) {
            AggregatorFactories.Builder builder = AggregatorFactories.builder();
            aggregations.forEach(aggregation -> builder.addAggregator(aggregation.toAggregationBuilder(indexObjectFields)));
            aggregationBuilder.subAggregations(builder);
        }
        return aggregationBuilder;
    }
}
