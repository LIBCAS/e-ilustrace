package cz.inqool.eas.common.domain.index.dto.aggregation;

import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

/**
 * A single-value metrics aggregation that calculates an approximate count of distinct values.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-cardinality-aggregation.html">Cardinality Aggregation</a>
 */
@Schema(
        description = "Cardinality Aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Cardinality Aggregation",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-cardinality-aggregation.html"
        )
)
@Getter
@Setter
public class CardinalityAggregation extends MetricAggregation {

    private String name;

    private String field;

    public CardinalityAggregation() {
        super(MetricAggregator.CARDINALITY);
    }

    @Builder
    public CardinalityAggregation(String name, String field) {
        super(MetricAggregator.CARDINALITY);
        this.name = name;
        this.field = field;
    }

    @Override
    public AggregationBuilder toAggregationBuilder(IndexObjectFields indexObjectFields) {
        return AggregationBuilders.cardinality(name).field(field);
    }
}
