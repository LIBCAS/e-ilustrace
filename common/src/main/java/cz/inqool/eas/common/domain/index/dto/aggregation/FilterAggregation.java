package cz.inqool.eas.common.domain.index.dto.aggregation;

import cz.inqool.eas.common.domain.index.dto.filter.Filter;
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
 * Defines a single bucket of all the documents in the current document set context that match a specified filter.
 * Often this will be used to narrow down the current aggregation context to a specific set of documents.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-filter-aggregation.html">Filter Aggregation</a>
 */
@Schema(
        description = "Filter aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Filter Aggregation",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-filter-aggregation.html"
        )
)
@Getter
@Setter
public class FilterAggregation extends BucketAggregation {

    private String name;

    private Filter filter;


    public FilterAggregation() {
        super(BucketAggregator.FILTER);
    }

    @Builder
    public FilterAggregation(String name, Filter filter, List<Aggregation> aggregations) {
        super(BucketAggregator.FILTER, aggregations);
        this.name = name;
        this.filter = filter;
    }


    @Override
    public AggregationBuilder toAggregationBuilder(IndexObjectFields indexObjectFields) {
        org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder aggregationBuilder =
                AggregationBuilders.filter(name, filter.toQueryBuilder(indexObjectFields));
        if (aggregations != null && !aggregations.isEmpty()) {
            AggregatorFactories.Builder builder = AggregatorFactories.builder();
            aggregations.forEach(aggregation -> builder.addAggregator(aggregation.toAggregationBuilder(indexObjectFields)));
            aggregationBuilder.subAggregations(builder);
        }
        return aggregationBuilder;
    }
}
