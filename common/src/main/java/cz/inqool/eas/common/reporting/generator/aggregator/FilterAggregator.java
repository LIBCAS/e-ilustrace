package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.domain.index.dto.aggregation.FilterAggregation;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.InFilter;
import cz.inqool.eas.common.reporting.exception.ReportAggregateGeneratorException;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.List.of;
import static java.util.stream.Collectors.toMap;

public class FilterAggregator extends ReportAggregator<FilterAggregator.Input> {
    protected final Bucket bucket;
    protected final MultiBucket multiBucket;

    @Builder
    public FilterAggregator(String name, String field, Input input, Bucket bucket, MultiBucket multiBucket, List<ReportAggregator<?>> subAggregators, String keyField, String keyLabel, Function<String, String> keyMapper, String countField, String countLabel, Boolean showKeyColumn, Boolean showCountColumn) {
        super(name, field, input, subAggregators, keyField, keyLabel, keyMapper, countField, countLabel, showKeyColumn, showCountColumn);
        this.bucket = bucket;
        this.multiBucket = multiBucket;
    }

    @Override
    public Aggregation aggregationFactory() {
        if (multiBucket == null && bucket == null) {
            throw new ReportAggregateGeneratorException("Need to specify bucket or multi bucket");
        } else if (multiBucket != null && bucket != null) {
            throw new ReportAggregateGeneratorException("Can't specify both bucket and multi bucket");
        } else if (multiBucket != null) {
            return FilterAggregation
                    .builder()
                    .name(this.getName())
                    .filter(new InFilter(this.getField(), multiBucket.getValues().toArray(String[]::new)))
                    .aggregations(this.subAggregationsFactory())
                    .build();
        } else {
            return FilterAggregation
                    .builder()
                    .name(this.getName())
                    .filter(new EqFilter(this.getField(), bucket.getValue()))
                    .aggregations(this.subAggregationsFactory())
                    .build();
        }


    }

    @Override
    public List<ReportColumn> subColumnsFactory() {
        return of(
                new ReportColumn(bucket.getLabel(), bucket.getValue(), 200, 100)
        );
    }

    @Override
    public Map<String, Object> parseResult(DefaultAggregationResult result) {
        Map<String, Object> data = new HashMap<>();

        if (showKeyColumn) {
            data.put(this.getKeyField(), bucket != null ? bucket.getLabel() : multiBucket.getLabel());
        }

        if (showCountColumn) {
            data.put(this.getCountField(), result.getValue());
        }

        return data;
    }

    @Override
    public Map<String, Object> parseSubResults(List<DefaultAggregationResult> result) {
        return result.stream().collect(toMap(item -> bucket.getValue(), DefaultAggregationResult::getValue));
    }

    @Data
    public static class Input {
    }

    @AllArgsConstructor
    @Data
    public static class Bucket {
        String value;
        String label;
    }

    @AllArgsConstructor
    @Data
    public static class MultiBucket {
        Collection<String> values;
        String label;
    }
}
