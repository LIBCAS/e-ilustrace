package cz.inqool.eas.common.domain.index.dto.aggregation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

/**
 * Abstract class for data transfer objects for ElasticSearch query aggregation.
 * <p>
 * Note that all instances of this class can be deserialized and therefore can be used both by front-end and back-end.
 */
@JsonDeserialize // to override parent annotation
@Getter
@Setter
@EqualsAndHashCode
@FieldNameConstants
abstract public class AbstractAggregation implements Aggregation {

    /**
     * Aggregation family used to define proper sub-class during deserialization.
     */
    @NotNull
    protected AggregationFamily family;


    public AbstractAggregation(@NotNull AggregationFamily family) {
        this.family = family;
    }


    /**
     * Enumeration of supported aggregation families
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html">Search Aggregations</a>
     */
    public enum AggregationFamily {

        /**
         * A family of aggregations that build buckets, where each bucket is associated with a key and a document
         * criterion. When the aggregation is executed, all the buckets criteria are evaluated on every document in the
         * context and when a criterion matches, the document is considered to "fall in" the relevant bucket. By the end
         * of the aggregation process, we’ll end up with a list of buckets - each one with a set of documents that
         * "belong" to it.
         *
         * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket.html#search-aggregations-bucket">Bucket Aggregations</a>
         */
        BUCKET,

        /**
         * A family of aggregations that operate on multiple fields and produce a matrix result based on the values
         * extracted from the requested document fields. Unlike metric and bucket aggregations, this aggregation family
         * does not yet support scripting.
         *
         * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-matrix.html">Matrix Aggregations</a>
         */
        MATRIX,

        /**
         * Aggregations that keep track and compute metrics over a set of documents.
         *
         * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics.html">Metric Aggregations</a>
         */
        METRIC,

        /**
         * Aggregations that aggregate the output of other aggregations and their associated metrics.
         *
         * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-pipeline.html">Pipeline Aggregations</a>
         */
        PIPELINE;


        /**
         * @see #BUCKET
         */
        public static final String BUCKET_VALUE = "BUCKET";

        /**
         * @see #MATRIX
         */
        public static final String MATRIX_VALUE = "MATRIX";

        /**
         * @see #METRIC
         */
        public static final String METRIC_VALUE = "METRIC";

        /**
         * @see #PIPELINE
         */
        public static final String PIPELINE_VALUE = "PIPELINE";
    }
}
