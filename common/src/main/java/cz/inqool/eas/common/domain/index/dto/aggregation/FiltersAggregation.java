package cz.inqool.eas.common.domain.index.dto.aggregation;

import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator;

import java.util.List;

/**
 * Defines a multi bucket aggregation where each bucket is associated with a filter. Each bucket will collect all
 * documents that match its associated filter.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-filters-aggregation.html">Filters Aggregation</a>
 */
@Schema(
        description = "Filters aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Filters Aggregation",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-filters-aggregation.html"
        )
)
@Getter
@Setter
public class FiltersAggregation extends BucketAggregation {

    private String name;
    private List<KeyedFilter> filters;

    /**
     * Set this parameter to add a bucket to the response which will contain all documents that do not match any of the
     * given filters.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-filters-aggregation.html#other-bucket">Other Bucket</a>
     */
    private Boolean otherBucket;

    /**
     * Used to set the key for the {@code other} bucket to a value other than the default {@code _other_}. Setting this
     * parameter will implicitly set the {@code other_bucket} parameter to true.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-filters-aggregation.html#other-bucket">Other Bucket</a>
     */
    private String otherBucketKey;


    public FiltersAggregation() {
        super(BucketAggregator.FILTERS);
    }

    @Builder
    public FiltersAggregation(String name, List<KeyedFilter> filters, Boolean otherBucket, String otherBucketKey, List<Aggregation> aggregations) {
        super(BucketAggregator.FILTERS, aggregations);
        this.name = name;
        this.filters = filters;
        this.otherBucket = otherBucket;
        this.otherBucketKey = otherBucketKey;
    }


    @Override
    public AggregationBuilder toAggregationBuilder(IndexObjectFields indexObjectFields) {
        FiltersAggregator.KeyedFilter[] keyedFilters = filters.stream()
                .map(f -> f.getKeyedFilter(indexObjectFields))
                .toArray(FiltersAggregator.KeyedFilter[]::new);
        org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder aggregationBuilder =
                AggregationBuilders.filters(name, keyedFilters);
        if (otherBucket != null) {
            aggregationBuilder.otherBucket(otherBucket);
        }
        if (otherBucketKey != null) {
            aggregationBuilder.otherBucketKey(otherBucketKey);
        }
        if (aggregations != null && !aggregations.isEmpty()) {
            AggregatorFactories.Builder builder = AggregatorFactories.builder();
            aggregations.forEach(aggregation -> builder.addAggregator(aggregation.toAggregationBuilder(indexObjectFields)));
            aggregationBuilder.subAggregations(builder);
        }
        return aggregationBuilder;
    }


    /**
     * This class represents a higher-level alternative of {@link org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator.KeyedFilter}.
     * It uses {@link Filter} instead of plain {@link QueryBuilder}.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyedFilter {

        private String key;
        private Filter filter;


        public org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator.KeyedFilter getKeyedFilter(IndexObjectFields indexedFields) {
            return new org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator.KeyedFilter(key, filter.toQueryBuilder(indexedFields));
        }
    }
}
