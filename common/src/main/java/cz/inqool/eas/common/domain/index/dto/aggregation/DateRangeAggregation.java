package cz.inqool.eas.common.domain.index.dto.aggregation;

import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.AggregatorFactories;

import java.util.List;

/**
 * A range aggregation that is dedicated for date values. The main difference between this aggregation and the
 * normal <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-range-aggregation.html">range</a>
 * aggregation is that the {@code from} and {@code to} values can be expressed in <a
 * href="https://www.elastic.co/guide/en/elasticsearch/reference/current/common-options.html#date-math">Date
 * Math</a> expressions, and it is also possible to specify a date format by which the {@code from} and {@code
 * to} response fields will be returned. Note that this aggregation includes the {@code from} value and excludes
 * the {@code to} value for each range.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-daterange-aggregation.html">Date Range Aggregation</a>
 */
@Schema(
        description = "Date Range aggregation",
        externalDocs = @ExternalDocumentation(
                description = "Date Range Aggregation",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-daterange-aggregation.html"
        )
)
@Getter
@Setter
public class DateRangeAggregation extends BucketAggregation {

    private String name;

    private String field;

    private List<DateRange> ranges;


    public DateRangeAggregation() {
        super(BucketAggregator.DATE_RANGE);
    }

    @Builder
    public DateRangeAggregation(String name, String field, List<DateRange> ranges, List<Aggregation> aggregations) {
        super(BucketAggregator.DATE_RANGE, aggregations);
        this.name = name;
        this.field = field;
        this.ranges = ranges;
    }


    @Override
    public AggregationBuilder toAggregationBuilder(IndexObjectFields indexObjectFields) {
        var aggregationBuilder = AggregationBuilders
                .dateRange(name)
                .field(field);

        ranges.forEach(range -> aggregationBuilder.addRange(range.key, range.from, range.to));

        if (aggregations != null && !aggregations.isEmpty()) {
            AggregatorFactories.Builder builder = AggregatorFactories.builder();
            aggregations.forEach(aggregation -> builder.addAggregator(aggregation.toAggregationBuilder(indexObjectFields)));
            aggregationBuilder.subAggregations(builder);
        }

        return aggregationBuilder;
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class DateRange {

        private String key;
        private String from;
        private String to;
    }
}
