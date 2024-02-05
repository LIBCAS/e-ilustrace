package cz.inqool.eas.common.reporting.generator.aggregator;

import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DateHistogramAggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.exception.GeneralException;
import cz.inqool.eas.common.reporting.input.ReportInputField;
import cz.inqool.eas.common.reporting.input.SelectReportInputField;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import lombok.Builder;
import lombok.Data;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;
import static java.util.List.of;

public class DateHistogramAggregator extends ReportAggregator<DateHistogramAggregator.Input> {
    private static final String AGGREGATION_FIELD = "created";

    private static final String INPUT_INTERVAL_FIELD = "interval";

    @Builder
    public DateHistogramAggregator(String name, String field, Input input, List<ReportAggregator<?>> subAggregators, String keyField, String keyLabel, Function<String, String> keyMapper, String countField, String countLabel, Boolean showKeyColumn, Boolean showCountColumn) {
        super(name, field != null ? field : AGGREGATION_FIELD, input, subAggregators, keyField, keyLabel != null ? keyLabel : getDefaultKeyLabel(input), keyMapper, countField, countLabel, showKeyColumn, showCountColumn);
    }

    @Override
    public Aggregation aggregationFactory() {
        Interval interval = coalesce(input.interval, DateHistogramAggregator::getDefaultInterval);

        DateHistogramInterval histogramInterval = getHistogramIntervalByInterval(interval);
        String format = getFormatForInterval(interval);

        return new DateHistogramAggregation(
                this.getName(),
                this.getField(),
                histogramInterval,
                format,
                this.subAggregationsFactory()
        );
    }

    public static List<ReportInputField> dateHistogramInputFieldsFactory() {
        return dateHistogramInputFieldsFactory(null, false);
    }

    public static List<ReportInputField> dateHistogramInputFieldsFactory(String prefix) {
        return dateHistogramInputFieldsFactory(prefix, false);
    }

    public static List<ReportInputField> dateHistogramInputFieldsFactory(String prefix, boolean multiple) {
        return of(
                new SelectReportInputField(
                        prefix != null ? prefix + "." + INPUT_INTERVAL_FIELD : INPUT_INTERVAL_FIELD,
                        "Dělení",
                        of(
                                new SelectReportInputField.SelectItem(Interval.DAY.name(), "Den"),
                                new SelectReportInputField.SelectItem(Interval.WEEK.name(), "Týden"),
                                new SelectReportInputField.SelectItem(Interval.MONTH.name(), "Měsíc"),
                                new SelectReportInputField.SelectItem(Interval.YEAR.name(), "Rok")
                        ),
                        multiple
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

    protected static String getDefaultKeyLabel(Input input) {
        Interval interval = coalesce(input.interval, DateHistogramAggregator::getDefaultInterval);
        return getColumnForIntervalLabel(interval);
    }

    protected static Interval getDefaultInterval() {
        return Interval.YEAR;
    }

    protected String getFormatForInterval(Interval interval) {
        switch (interval) {
            case DAY:
                return "DDD";
            case WEEK:
                return "ww";
            case MONTH:
                return "MM";
            case YEAR:
                return "yyyy";
            default:
                throw new GeneralException("Unsupported interval specified.");
        }
    }

    protected DateHistogramInterval getHistogramIntervalByInterval(Interval interval) {
        switch (interval) {
            case DAY:
                return DateHistogramInterval.DAY;
            case WEEK:
                return DateHistogramInterval.WEEK;
            case MONTH:
                return DateHistogramInterval.MONTH;
            case YEAR:
                return DateHistogramInterval.YEAR;
            default:
                throw new GeneralException("Unsupported interval specified.");
        }
    }

    protected static String getColumnForIntervalLabel(Interval interval) {
        switch (interval) {
            case DAY:
                return "Den";
            case WEEK:
                return "Týden";
            case MONTH:
                return "Měsíc";
            case YEAR:
                return "Rok";
            default:
                throw new UnsupportedOperationException("Unsupported interval specified.");
        }
    }

    public enum Interval {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    @Data
    public static class Input {
        private Interval interval;
    }
}
