package cz.inqool.eas.common.domain.index.dto.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import static cz.inqool.eas.common.domain.index.dto.aggregation.AbstractAggregation.AggregationFamily.BUCKET_VALUE;
import static cz.inqool.eas.common.domain.index.dto.aggregation.AbstractAggregation.AggregationFamily.METRIC_VALUE;

/**
 * Represents an ElasticSearch search aggregation.
 * <p>
 * The aggregations framework helps provide aggregated data based on a search query. It is based on simple building
 * blocks called aggregations, that can be composed in order to build complex summaries of the data.
 * <p>
 * An aggregation can be seen as a unit-of-work that builds analytic information over a set of documents. The context of
 * the execution defines what this document set is (e.g. a top-level aggregation executes within the context of the
 * executed query/filters of the search request).
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html">Aggregations</a>
 */
@JsonDeserialize(using = AggregationDeserializer.class)
@JsonIgnoreProperties // to disable ignore_unknown deserialization feature
@Schema(
        description = "Aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Aggregations",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html"
        ),
        oneOf = {
                BucketAggregation.class,
                MetricAggregation.class
        },
        discriminatorProperty = AbstractAggregation.Fields.family,
        discriminatorMapping = {
                @DiscriminatorMapping(value = BUCKET_VALUE, schema = BucketAggregation.class),
                @DiscriminatorMapping(value = METRIC_VALUE, schema = MetricAggregation.class)
        }
)
public interface Aggregation {

    /**
     * Returns an elastic search aggregation builder representing aggregation definition.
     *
     * @param indexObjectFields supported indexed fields
     * @return created aggregation builder
     */
    AggregationBuilder toAggregationBuilder(IndexObjectFields indexObjectFields);
}
