package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.domain.index.dto.aggregation.TermsAggregation;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.Builder;
import lombok.Data;
import org.elasticsearch.script.Script;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TermsAggregator extends ReportAggregator<TermsAggregator.Input> {

    private final Script script;

    @Builder
    public TermsAggregator(String name, String field, Input input, List<ReportAggregator<?>> subAggregators, String keyField, String keyLabel, Function<String, String> keyMapper, String countField, String countLabel, Boolean showKeyColumn, Boolean showCountColumn, Script script) {
        super(name, field, input, subAggregators, keyField, keyLabel, keyMapper, countField, countLabel, showKeyColumn, showCountColumn);
        this.script = script;
    }

    @Override
    public Aggregation aggregationFactory() {
        return TermsAggregation
                .builder()
                .name(this.getName())
                .field(this.getField())
                .script(script)
                .aggregations(this.subAggregationsFactory())
                .build();
    }

    @Override
    public List<ReportColumn> subColumnsFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Map<String, Object> parseResult(DefaultAggregationResult result) {
        return super.parseResult(result);
    }

    @Override
    public Map<String, Object> parseSubResults(List<DefaultAggregationResult> result) {
        throw new UnsupportedOperationException();
    }

    @Data
    public static class Input {
    }
}
