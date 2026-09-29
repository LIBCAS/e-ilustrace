package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.domain.index.dto.aggregation.NestedAggregation;
import cz.inqool.eas.common.reporting.exception.ReportAggregateGeneratorException;
import cz.inqool.eas.common.reporting.generator.ReportGenerator;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static cz.inqool.eas.common.utils.AssertionUtils.isTrue;

public class NestedAggregator extends ReportAggregator<NestedAggregator.Input> {
    @Builder
    public NestedAggregator(String name, String field, Input input, List<ReportAggregator<?>> subAggregators, String keyField, String keyLabel, Function<String, String> keyMapper, String countField, String countLabel) {
        super(name, field, input, subAggregators, keyField, keyLabel, keyMapper, countField, countLabel, false, false);
    }

    @Override
    public Aggregation aggregationFactory() {
        return NestedAggregation
                .builder()
                .name(this.getName())
                .path(this.getField())
                .aggregations(this.subAggregationsFactory())
                .build();
    }

    @Override
    public List<ReportColumn> subColumnsFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReportGenerator.GeneratorResult<Map<String, Object>> processResults(List<DefaultAggregationResult> results) {
        DefaultAggregationResult result = results.get(0);

        List<ReportColumn> columns = new ArrayList<>();
        List<Map<String, Object>> items = new ArrayList<>();

        this.subAggregators.forEach(aggregator -> {
            @SuppressWarnings("unchecked")
            List<DefaultAggregationResult> aggregationItems = (List<DefaultAggregationResult>) result.getAggregations().get(aggregator.getName());
            ReportGenerator.GeneratorResult<Map<String, Object>> partialResult = aggregator.processResults(aggregationItems);

            if (columns.size() == 0) {
                columns.addAll(partialResult.getColumns());
            } else {
                isTrue(columns.equals(partialResult.getColumns()), () -> new ReportAggregateGeneratorException("Wrong definition of report"));
            }

            items.addAll(partialResult.getItems());
        });

        return new ReportGenerator.GeneratorResult<>(columns, items);
    }

    @Override
    public Map<String, Object> parseSubResults(List<DefaultAggregationResult> result) {
        throw new UnsupportedOperationException();
    }

    @Data
    public static class Input {
    }
}
