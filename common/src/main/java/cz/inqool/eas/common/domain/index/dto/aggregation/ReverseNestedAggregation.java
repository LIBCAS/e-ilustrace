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
 * A special single bucket aggregation that enables aggregating on parent docs from nested documents.
 * Effectively this aggregation can break out of the nested block structure and link to other nested
 * structures or the root document, which allows nesting other aggregations that aren’t part of the nested
 * object in a nested aggregation.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-reverse-nested-aggregation.html">Reverse nested aggregation</a>
 */
@Schema(
        description = "Reverse nested aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Reverse nested aggregation",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-reverse-nested-aggregation.html"
        )
)
@Getter
@Setter
public class ReverseNestedAggregation extends BucketAggregation {

    private String name;


    public ReverseNestedAggregation() {
        super(BucketAggregator.REVERSE_NESTED);
    }

    @Builder
    public ReverseNestedAggregation(String name, List<Aggregation> aggregations) {
        super(BucketAggregator.REVERSE_NESTED, aggregations);
        this.name = name;
    }


    @Override
    public AggregationBuilder toAggregationBuilder(IndexObjectFields indexObjectFields) {
        org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder aggregationBuilder =
                AggregationBuilders.reverseNested(name);
        if (aggregations != null && !aggregations.isEmpty()) {
            AggregatorFactories.Builder builder = AggregatorFactories.builder();
            aggregations.forEach(aggregation -> builder.addAggregator(aggregation.toAggregationBuilder(indexObjectFields)));
            aggregationBuilder.subAggregations(builder);
        }
        return aggregationBuilder;
    }
}
