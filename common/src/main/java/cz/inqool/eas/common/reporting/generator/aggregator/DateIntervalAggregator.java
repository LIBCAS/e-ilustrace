package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DateRangeAggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DateRangeAggregation.DateRange;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.reporting.input.ReportInputField;
import cz.inqool.eas.common.reporting.input.ReportInputFieldType;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.List.of;

public class DateIntervalAggregator extends ReportAggregator<DateIntervalAggregator.Input> {
    private static final String AGGREGATION_FIELD = "created";

    private static final String INPUT_FROM_FIELD = "from";
    private static final String INPUT_TO_FIELD = "to";

    private static final DateTimeFormatter formatter = ofPattern("dd.MM.yyyyy");

    @Builder
    public DateIntervalAggregator(String name, String field, Input input, List<ReportAggregator<?>> subAggregators, String keyField, String keyLabel, Function<String, String> keyMapper, String countField, String countLabel, Boolean showKeyColumn, Boolean showCountColumn) {
        super(name, field != null ? field : AGGREGATION_FIELD, input, subAggregators, keyField, keyLabel != null ? keyLabel : "Interval", keyMapper, countField, countLabel, showKeyColumn, showCountColumn);
    }

    @Override
    public Aggregation aggregationFactory() {
        String from = input.getFrom();
        String fromString = from != null ? getFormatter().format(LocalDate.parse(from)) : "*";

        String to = input.getTo();
        String toString = to != null ? getFormatter().format(LocalDate.parse(to)) : "*";

        String key = fromString + " - " + toString;

        return new DateRangeAggregation(
                this.getName(),
                this.getField(),
                of(new DateRange(key, input.from, input.to)),
                this.subAggregationsFactory()
        );
    }

    public static List<ReportInputField> dateIntervalInputFieldsFactory(String prefix) {
        return of(
                new ReportInputField(
                        prefix != null ? prefix + "." + INPUT_FROM_FIELD : INPUT_FROM_FIELD,
                        "Od",
                        ReportInputFieldType.DATE
                ),
                new ReportInputField(
                        prefix != null ? prefix + "." + INPUT_TO_FIELD : INPUT_TO_FIELD,
                        "Do",
                        ReportInputFieldType.DATE
                )
        );
    }

    @Override
    public List<ReportColumn> subColumnsFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> parseSubResults(List<DefaultAggregationResult> result) {
        throw new UnsupportedOperationException();
    }

    protected DateTimeFormatter getFormatter() {
        return formatter;
    }

    @Data
    public static class Input {
        private String from;
        private String to;
    }
}
