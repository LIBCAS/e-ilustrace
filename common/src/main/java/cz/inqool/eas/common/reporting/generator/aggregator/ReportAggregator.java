package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.reporting.generator.ReportGenerator;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;

public abstract class ReportAggregator<INPUT> {
    @Getter
    protected String name;

    protected String field;

    protected INPUT input;

    protected List<ReportAggregator<?>> subAggregators;

    protected String keyField;

    protected String keyLabel;

    protected Function<String, String> keyMapper;

    protected String countField;

    protected String countLabel;

    protected boolean showKeyColumn;

    protected boolean showCountColumn;

    public ReportAggregator(String name, String field, INPUT input, List<ReportAggregator<?>> subAggregators, String keyField, String keyLabel, Function<String, String> keyMapper, String countField, String countLabel, Boolean showKeyColumn, Boolean showCountColumn) {
        this.name = coalesce(name, () -> "aggregation");
        this.field = field;
        this.input = input;
        this.subAggregators = subAggregators;
        this.keyField = coalesce(keyField, () -> "key");
        this.keyLabel = coalesce(keyLabel, () -> "Hodnota");
        this.keyMapper = coalesce(keyMapper, () -> (key) -> key);
        this.countField = coalesce(countField, () -> "count");
        this.countLabel = coalesce(countLabel, () -> "Celkový počet");
        this.showKeyColumn = coalesce(showKeyColumn, () -> true);
        this.showCountColumn = coalesce(showCountColumn, () -> true);
    }

    public String getField() {
        return field;
    }

    public String getKeyField() {
        return keyField;
    }

    public String getKeyLabel() {
        return keyLabel;
    }

    public String getCountField() {
        return countField;
    }

    public String getCountLabel() {
        return countLabel;
    }

    public ReportGenerator.GeneratorResult<Map<String, Object>> processResults(List<DefaultAggregationResult> results) {
        List<ReportColumn> columns = new ArrayList<>();
        columns.addAll(this.columnsFactory());

        columns.addAll(
                coalesce(this.subAggregators, List::<ReportAggregator<?>>of)
                    .stream()
                    .map(ReportAggregator::subColumnsFactory)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
        );

        List<Map<String, Object>> items = results
                .stream()
                .map(item -> {
                    Map<String, Object> data = new LinkedHashMap<>();
                    data.putAll(this.parseResult(item));

                    data.putAll(coalesce(this.subAggregators, List::<ReportAggregator<?>>of)
                            .stream()
                            .map(subAggregator -> {
                                @SuppressWarnings("unchecked")
                                List<DefaultAggregationResult> aggregationResults = (List<DefaultAggregationResult>) item.getAggregations().get(subAggregator.getName());
                                return subAggregator.parseSubResults(aggregationResults);
                            })
                            .flatMap(d -> d.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    );

                    return data;
                }).collect(Collectors.toList());

        return new ReportGenerator.GeneratorResult<>(columns, items);
    }

    protected List<Aggregation> subAggregationsFactory() {
        return coalesce(this.subAggregators, List::<ReportAggregator<?>>of)
                .stream()
                .map(ReportAggregator::aggregationFactory)
                .collect(Collectors.toList());
    }

    protected List<ReportColumn> columnsFactory() {
        List<ReportColumn> columns = new ArrayList<>();

        if (showKeyColumn) {
            columns.add(new ReportColumn(this.getKeyLabel(), this.getKeyField(), 200, 100));
        }

        if (showCountColumn) {
            columns.add(new ReportColumn(this.getCountLabel(), this.getCountField(), 200, 100));
        }

        return columns;
    }

    protected Map<String, Object> parseResult(DefaultAggregationResult result) {
        Map<String, Object> data = new HashMap<>();

        if (showKeyColumn) {
            data.put(this.getKeyField(), keyMapper.apply(result.getKey()));
        }

        if (showCountColumn) {
            data.put(this.getCountField(), result.getValue());
        }

        return data;
    }

    public abstract Aggregation aggregationFactory();

    protected abstract List<ReportColumn> subColumnsFactory();

    protected abstract Map<String, Object> parseSubResults(List<DefaultAggregationResult> results);
}
