package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.Data;

import java.util.List;
import java.util.Map;

import static java.util.List.of;

public class EmptyAggregator extends ReportAggregator<Void> {
    public EmptyAggregator() {
        super(null,null, null, null, null, null, null, null, null, false, false);
    }

    @Override
    public Aggregation aggregationFactory() {
        return null;
    }

    @Override
    public List<ReportColumn> subColumnsFactory() {
        return of();
    }

    @Override
    public Map<String, Object> parseSubResults(List<DefaultAggregationResult> result) {
        return Map.of();
    }

    @Override
    public List<ReportColumn> columnsFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> parseResult(DefaultAggregationResult result) {
        throw new UnsupportedOperationException();
    }

    @Data
    public static class Input {
    }
}
