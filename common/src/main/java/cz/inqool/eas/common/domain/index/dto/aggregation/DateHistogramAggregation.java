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
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.util.List;

/**
 * This multi-bucket aggregation is similar to the normal <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-histogram-aggregation.html">histogram</a>,
 * but it can only be used with date values. Because dates are represented internally in Elasticsearch as long
 * values, it is possible, but not as accurate, to use the normal {@code histogram} on dates as well. The main
 * difference in the two APIs is that here the interval can be specified using date/time expressions. Time-based
 * data requires special support because time-based intervals are not always a fixed length.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-datehistogram-aggregation.html">Date Histogram Aggregation</a>
 */
@Schema(
        description = "Date Histogram Aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Date Histogram Aggregation",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-datehistogram-aggregation.html"
        )
)
@Getter
@Setter
public class DateHistogramAggregation extends BucketAggregation {

    private String name;

    private String field;

    private String format;

    private DateHistogramInterval interval;

    public DateHistogramAggregation() {
        super(BucketAggregator.DATE_HISTOGRAM);
    }

    @Builder
    public DateHistogramAggregation(String name, String field, DateHistogramInterval interval, String format, List<Aggregation> aggregations) {
        super(BucketAggregator.DATE_HISTOGRAM, aggregations);
        this.name = name;
        this.field = field;
        this.interval = interval;
        this.format = format;
    }

    @Override
    public AggregationBuilder toAggregationBuilder(IndexObjectFields indexObjectFields) {
        var aggregationBuilder = AggregationBuilders
                .dateHistogram(name)
                .field(field)
                .calendarInterval(interval);

        if (format != null) {
            aggregationBuilder.format(format);
        }

        if (aggregations != null && !aggregations.isEmpty()) {
            AggregatorFactories.Builder builder = AggregatorFactories.builder();
            aggregations.forEach(aggregation -> builder.addAggregator(aggregation.toAggregationBuilder(indexObjectFields)));
            aggregationBuilder.subAggregations(builder);
        }

        return aggregationBuilder;
    }
}
