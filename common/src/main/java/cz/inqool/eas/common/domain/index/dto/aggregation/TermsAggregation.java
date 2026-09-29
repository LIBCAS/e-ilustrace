package cz.inqool.eas.common.domain.index.dto.aggregation;

import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import cz.inqool.eas.common.exception.v2.IndexException;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregator.SubAggCollectionMode;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;

import javax.validation.constraints.Min;
import java.util.List;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.FIELD_NOT_INDEXED;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.FIELD_NOT_MAPPED;

/**
 * A multi-bucket value source based aggregation where buckets are dynamically built - one per unique value.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html">Terms Aggregation</a>
 */
@SuppressWarnings("JavadocReference")
@Schema(
        description = "Terms Aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Terms Aggregation",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html"
        )
)
@Getter
@Setter
public class TermsAggregation extends BucketAggregation {

    /**
     * Name of this aggregation.
     *
     * @see AggregationBuilder#name
     */
    private String name;

    /**
     * Sets the field to use for this aggregation.
     *
     * @see ValuesSourceAggregationBuilder#field
     */
    private String field;

    /**
     * The {@code size} parameter can be set to define how many term buckets should be returned out of the overall terms
     * list. By default, the node coordinating the search process will request each shard to provide its own top {@code
     * size} term buckets and once all shards respond, it will reduce the results to the final list that will then be
     * returned to the client. This means that if the number of unique terms is greater than {@code size}, the returned
     * list is slightly off and not accurate (it could be that the term counts are slightly off and it could even be
     * that a term that should have been in the top size buckets was not returned).
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#search-aggregations-bucket-terms-aggregation-size">Size</a>
     */
    @Min(1)
    private Integer size;

    /**
     * The higher the requested {@code size} is, the more accurate the results will be, but also, the more expensive it
     * will be to compute the final results (both due to bigger priority queues that are managed on a shard level and
     * due to bigger data transfers between the nodes and the client).
     * <p>
     * The {@code shard_size} parameter can be used to minimize the extra work that comes with bigger requested {@code
     * size}. When defined, it will determine how many terms the coordinating node will request from each shard. Once
     * all the shards responded, the coordinating node will then reduce them to a final result which will be based on
     * the {@code size} parameter - this way, one can increase the accuracy of the returned terms and avoid the overhead
     * of streaming a big list of buckets back to the client.
     * <p>
     * The default {@code shard_size} will be {@code size} if the search request needs to go to a single shard, and
     * {@code (size * 1.5 + 10)} otherwise.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_shard_size_3">Shard
     * Size</a>
     */
    @Min(1)
    private Integer shardSize;

    /**
     * If {@code true}, shows an error value for each term returned by the aggregation which represents the worst case
     * error in the document count and can be useful when deciding on a value for the {@code shard_size} parameter. This
     * is calculated by summing the document counts for the last term returned by all shards which did not return the
     * term.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_per_bucket_document_count_error">Per
     * bucket document count error</a>
     */
    private Boolean showTermDocCountError;

    /**
     * The order of the buckets can be customized by setting the {@code order} parameter. By default, the buckets are
     * ordered by their {@code doc_count} descending.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#search-aggregations-bucket-terms-aggregation-order">Order</a>
     */
    private List<BucketOrder> orders;

    /**
     * It is possible to only return terms that match more than a configured number of hits using the {@code
     * min_doc_count} option. Default value is {@code 1}.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_minimum_document_count_4">Minimum
     * document count</a>
     */
    @Min(1)
    private Long minDocCount;

    /**
     * The parameter {@code shard_min_doc_count} regulates the certainty a shard has if the term should actually be
     * added to the candidate list or not with respect to the {@code min_doc_count}. Terms will only be considered if
     * their local shard frequency within the set is higher than the {@code shard_min_doc_count}. If your dictionary
     * contains many low frequent terms and you are not interested in those (for example misspellings), then you can set
     * the {@code shard_min_doc_count} parameter to filter out candidate terms on a shard level that will with a
     * reasonable certainty not reach the required {@code min_doc_count} even after merging the local counts. {@code
     * shard_min_doc_count} is set to 0 per default and has no effect unless you explicitly set it.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_minimum_document_count_4">Shard
     * minimum document count</a>
     */
    @Min(0)
    private Long shardMinDocCount;

    /**
     * Generating the terms using a script.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#search-aggregations-bucket-terms-aggregation-script">Script</a>
     */
    private Script script;

    /**
     * It is possible to filter the values for which buckets will be created. This can be done using the {@code include}
     * and {@code exclude} parameters which are based on regular expression strings or arrays of exact values.
     * Additionally, {@code include} clauses can filter using {@code partition} expressions.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_filtering_values_3">Filtering
     * Values</a>
     */
    private IncludeExclude includeExclude;

    /**
     * Deferring calculation of child aggregations.
     * <p>
     * For fields with many unique terms and a small number of required results it can be more efficient to delay the
     * calculation of child aggregations until the top parent-level aggs have been pruned. Ordinarily, all branches of
     * the aggregation tree are expanded in one depth-first pass and only then any pruning occurs. In some scenarios
     * this can be very wasteful and can hit memory constraints.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_collect_mode">Collect
     * mode</a>
     */
    private SubAggCollectionMode collectMode;

    /**
     * There are different mechanisms by which terms aggregations can be executed:
     * <ul>
     *   <li>by using field values directly in order to aggregate data per-bucket ({@code map})</li>
     *   <li>by using global ordinals of the field and allocating one bucket per global ordinal ({@code global_ordinals})</li>
     * </ul>
     * <p>
     * Elasticsearch tries to have sensible defaults so this is something that generally doesn’t need to be configured.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#search-aggregations-bucket-terms-aggregation-execution-hint">Execution
     * hint</a>
     */
    private ExecutionHint executionHint;

    /**
     * The {@code missing} parameter defines how documents that are missing a value should be treated. By default they
     * will be ignored but it is also possible to treat them as if they had a value.
     *
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_missing_value_12">Missing
     * value</a>
     */
    private Object missing;


    public TermsAggregation() {
        super(BucketAggregator.TERMS);
    }

    @Builder
    public TermsAggregation(List<Aggregation> aggregations, String name, String field,
                            @Min(1) Integer size, @Min(1) Integer shardSize, Boolean showTermDocCountError, List<BucketOrder> orders,
                            @Min(1) Long minDocCount, @Min(0) Long shardMinDocCount, Script script, IncludeExclude includeExclude,
                            SubAggCollectionMode collectMode, ExecutionHint executionHint, Object missing) {
        super(BucketAggregator.TERMS);
        this.name = name;
        this.field = field;
        this.size = size;
        this.shardSize = shardSize;
        this.showTermDocCountError = showTermDocCountError;
        this.orders = orders;
        this.minDocCount = minDocCount;
        this.shardMinDocCount = shardMinDocCount;
        this.script = script;
        this.includeExclude = includeExclude;
        this.collectMode = collectMode;
        this.executionHint = executionHint;
        this.missing = missing;
        this.aggregations = aggregations;
    }


    @Override
    public AggregationBuilder toAggregationBuilder(IndexObjectFields indexObjectFields) {
        org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(name);
        if (field != null) {
            if (!indexObjectFields.containsKey(field)) {
                throw new IndexException(FIELD_NOT_MAPPED)
                        .details(details -> details.property("field", field))
                        .debugInfo(info -> info.property("class", TermsAggregation.class));
            }
            IndexFieldLeafNode leafNode = indexObjectFields.get(field, IndexFieldLeafNode.class);
            if (!leafNode.isIndexed()) {
                throw new IndexException(FIELD_NOT_INDEXED)
                        .details(details -> details.property("field", field))
                        .debugInfo(info -> info.property("class", TermsAggregation.class));
            }

            aggregationBuilder.field(leafNode.getSortable());
        }
        if (size != null) {
            aggregationBuilder.size(size);
        }
        if (shardSize != null) {
            aggregationBuilder.shardSize(shardSize);
        }
        if (showTermDocCountError != null) {
            aggregationBuilder.showTermDocCountError(showTermDocCountError);
        }
        if (orders != null && !orders.isEmpty()) {
            aggregationBuilder.order(orders);
        }
        if (minDocCount != null) {
            aggregationBuilder.minDocCount(minDocCount);
        }
        if (shardMinDocCount != null) {
            aggregationBuilder.shardMinDocCount(shardMinDocCount);
        }
        if (script != null) {
            aggregationBuilder.script(script);
        }
        if (includeExclude != null) {
            aggregationBuilder.includeExclude(includeExclude);
        }
        if (collectMode != null) {
            aggregationBuilder.collectMode(collectMode);
        }
        if (executionHint != null) {
            aggregationBuilder.executionHint(executionHint.name());
        }
        if (missing != null) {
            aggregationBuilder.missing(missing);
        }
        if (aggregations != null && !aggregations.isEmpty()) {
            AggregatorFactories.Builder builder = AggregatorFactories.builder();
            aggregations.forEach(aggregation -> builder.addAggregator(aggregation.toAggregationBuilder(indexObjectFields)));
            aggregationBuilder.subAggregations(builder);
        }
        return aggregationBuilder;
    }


    /**
     * Mechanisms by which terms aggregations can be executed.
     */
    private enum ExecutionHint {
        /**
         * {@code map} should only be considered when very few documents match a query. Otherwise the ordinals-based
         * execution mode is significantly faster. By default, {@code map} is only used when running an aggregation on
         * scripts, since they don’t have ordinals.
         */
        map,

        /**
         * {@code global_ordinals} is the default option for {@code keyword} field, it uses global ordinals to allocates
         * buckets dynamically so memory usage is linear to the number of values of the documents that are part of the
         * aggregation scope.
         */
        global_ordinals
    }
}
