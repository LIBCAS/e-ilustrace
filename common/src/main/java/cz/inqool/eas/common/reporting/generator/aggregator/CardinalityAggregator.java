package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.CardinalityAggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.reporting.generator.ReportGenerator;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.List.of;

public class CardinalityAggregator extends ReportAggregator<CardinalityAggregator.Input> {
    @Builder
    public CardinalityAggregator(String name, String field, Input input, String keyField, String keyLabel, Function<String, String> keyMapper, String countField, String countLabel) {
        super(name, field, input, null, keyField, keyLabel, keyMapper, countField, countLabel, false, false);
    }

    @Override
    public Aggregation aggregationFactory() {
        return CardinalityAggregation
                .builder()
                .name(this.getName())
                .field(this.getField())
                .build();
    }

    @Override
    public ReportGenerator.GeneratorResult<Map<String, Object>> processResults(List<DefaultAggregationResult> results) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<ReportColumn> columnsFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ReportColumn> subColumnsFactory() {
        return of(
                new ReportColumn(this.getCountLabel(), this.getCountField(), 200, 100)
        );
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
