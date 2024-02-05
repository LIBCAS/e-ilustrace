package cz.inqool.eas.common.domain.index.dto.aggregation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.index.dto.aggregation.MetricAggregation.Fields.aggregator;
import static cz.inqool.eas.common.domain.index.dto.aggregation.MetricAggregation.MetricAggregator.CARDINALITY_VALUE;

/**
 * The aggregations in this family compute metrics based on values extracted in one way or another from the documents
 * that are being aggregated. The values are typically extracted from the fields of the document (using the field data),
 * but can also be generated using scripts.
 *
 * Numeric metrics aggregations are a special type of metrics aggregation which output numeric values. Some aggregations
 * output a single numeric metric (e.g. avg) and are called single-value numeric metrics aggregation, others generate
 * multiple metrics (e.g. stats) and are called multi-value numeric metrics aggregation. The distinction between
 * single-value and multi-value numeric metrics aggregations plays a role when these aggregations serve as direct
 * sub-aggregations of some bucket aggregations (some bucket aggregations enable you to sort the returned buckets
 * based on the numeric metrics in each bucket).
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics.html">Metric
 * Aggregations</a>
 */
@Schema(
        description = "Metric Aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Metric Aggregation",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics.html"
        ),
        oneOf = {
                CardinalityAggregation.class
        },
        discriminatorProperty = aggregator,
        discriminatorMapping = {
                @DiscriminatorMapping(value = CARDINALITY_VALUE, schema = CardinalityAggregation.class)
        }
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = aggregator,
        visible = true)
@JsonSubTypes({
        @Type(name = CARDINALITY_VALUE, value = CardinalityAggregation.class)
})
@Getter
@Setter
@FieldNameConstants
abstract public class MetricAggregation extends AbstractAggregation {

    /**
     * Metric aggregator
     *
     * @see MetricAggregator
     */
    protected MetricAggregator aggregator;


    public MetricAggregation(@NotNull MetricAggregator aggregator) {
        super(AggregationFamily.METRIC);
        this.aggregator = aggregator;
    }


    public enum MetricAggregator {

        /**
         * A single-value metrics aggregation that calculates an approximate count of distinct values.
         *
         * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-cardinality-aggregation.html">Cardinality
         * Aggregation</a>
         */
        CARDINALITY;


        /**
         * @see #CARDINALITY
         */
        public static final String CARDINALITY_VALUE = "CARDINALITY";
    }
}
