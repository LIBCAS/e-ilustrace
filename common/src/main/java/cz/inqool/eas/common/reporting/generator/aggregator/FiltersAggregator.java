package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.domain.index.dto.aggregation.FiltersAggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.FiltersAggregation.KeyedFilter;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class FiltersAggregator extends ReportAggregator<FiltersAggregator.Input> {
    protected final List<Bucket> buckets;
    protected final Map<String, Bucket> bucketMap;

    @Builder
    public FiltersAggregator(String name, String field, Input input, List<Bucket> buckets, List<ReportAggregator<?>> subAggregators, String keyField, Function<String, String> keyMapper, String keyLabel, String countField, String countLabel, Boolean showKeyColumn, Boolean showCountColumn) {
        super(name, field, input, subAggregators, keyField, keyLabel, keyMapper, countField, countLabel, showKeyColumn, showCountColumn);
        this.buckets = buckets;
        this.bucketMap = buckets.stream().collect(toMap(Bucket::getValue, identity()));
    }

    @Override
    public Aggregation aggregationFactory() {
        return FiltersAggregation
                .builder()
                .name(this.getName())
                .filters(this.buckets
                        .stream()
                        .map(bucket -> new KeyedFilter(bucket.getValue(), new EqFilter(this.getField(), bucket.getValue())))
                        .collect(toList()))
                .aggregations(subAggregationsFactory())
                .build();
    }

    @Override
    public List<ReportColumn> subColumnsFactory() {
        return buckets
                .stream()
                .map(bucket -> new ReportColumn(bucket.getLabel(), bucket.getValue(), 200, 100))
                .collect(toList());
    }

    @Override
    public Map<String, Object> parseResult(DefaultAggregationResult result) {
        Bucket bucket = bucketMap.get(result.getKey());

        Map<String, Object> data = new HashMap<>();

        if (showKeyColumn) {
            data.put(this.getKeyField(), bucket.getLabel());
        }

        if (showCountColumn) {
            data.put(this.getCountField(), result.getValue());
        }

        return data;
    }

    @Override
    public Map<String, Object> parseSubResults(List<DefaultAggregationResult> result) {
        return result.stream().collect(toMap(item -> {
            Bucket bucket = bucketMap.get(item.getKey());
            return bucket.getValue();
        }, DefaultAggregationResult::getValue));
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
}
