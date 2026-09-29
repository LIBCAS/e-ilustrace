package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.domain.index.dto.aggregation.ReverseNestedAggregation;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.List.of;

public class ReverseNestedAggregator extends ReportAggregator<ReverseNestedAggregator.Input> {

    @Builder
    public ReverseNestedAggregator(String name, Input input, List<ReportAggregator<?>> subAggregators, String keyField, String keyLabel, Function<String, String> keyMapper, String countField, String countLabel) {
        super(name, "dummy", input, subAggregators, keyField, keyLabel, keyMapper, countField, countLabel, false, false);
    }

    @Override
    public Aggregation aggregationFactory() {
        return ReverseNestedAggregation
                .builder()
                .name(this.getName())
                .aggregations(this.subAggregationsFactory())
                .build();
    }

    @Override
    public List<ReportColumn> subColumnsFactory() {
        return of(
                new ReportColumn(this.getCountLabel(), this.getCountField(), 200, 100)
        );
    }

    @Override
    public Map<String, Object> parseResult(DefaultAggregationResult result) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> parseSubResults(List<DefaultAggregationResult> results) {
        DefaultAggregationResult result = results.get(0);
        return Map.of(this.getCountField(), result.getValue());
    }

    @Data
    public static class Input {
    }
}
