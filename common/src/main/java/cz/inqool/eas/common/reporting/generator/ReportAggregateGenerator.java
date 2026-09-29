package cz.inqool.eas.common.reporting.generator;

import cz.inqool.eas.common.domain.index.DomainIndex;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.aggregation.DefaultAggregationResult;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.reporting.exception.ReportAggregateGeneratorException;
import cz.inqool.eas.common.reporting.generator.aggregator.EmptyAggregator;
import cz.inqool.eas.common.reporting.generator.aggregator.ReportAggregator;
import cz.inqool.eas.common.reporting.report.ReportColumn;

import java.util.*;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.AssertionUtils.isTrue;

public abstract class ReportAggregateGenerator<INPUT> extends ReportGenerator<INPUT, Map<String, Object>> {
    public ReportAggregateGenerator(Class<INPUT> configurationClass) {
        super(configurationClass);
    }

    @Override
    protected GeneratorResult<Map<String, Object>> generateInternal(INPUT input) {
        List<ReportAggregator<?>> aggregators = aggregatorsFactory(input);

        List<Aggregation> aggregations = aggregators
                .stream()
                .map(ReportAggregator::aggregationFactory)
                .collect(Collectors.toList());

        List<Filter> filters = filtersFactory(input);

        Params params = new Params();
        params.setAggregations(aggregations);
        params.setFilters(filters);

        Result<String> result = this.getIndex(input).listIdsByParams(params);

        List<ReportColumn> columns = new ArrayList<>();
        List<Map<String, Object>> items = new ArrayList<>();

        aggregators.forEach(aggregator -> {
            @SuppressWarnings("unchecked")
            List<DefaultAggregationResult> aggregationItems = (List<DefaultAggregationResult>) result.getAggregations().get(aggregator.getName());
            GeneratorResult<Map<String, Object>> partialResult = aggregator.processResults(aggregationItems);

            if (columns.size() == 0) {
                columns.addAll(partialResult.getColumns());
            } else {
                isTrue(columns.equals(partialResult.getColumns()), () -> new ReportAggregateGeneratorException("Wrong definition of report"));
            }

            items.addAll(partialResult.getItems());
        });

        return new  GeneratorResult<>(columns, items);
    }

    public List<Filter> filtersFactory(INPUT input) {
        return List.of();
    }

    public List<ReportAggregator<?>> aggregatorsFactory(INPUT input) {
        return List.of(aggregatorFactory(input));
    }

    public ReportAggregator<?> aggregatorFactory(INPUT input) {
        return new EmptyAggregator();
    }

    protected abstract DomainIndex<?, ?, ?> getIndex(INPUT input);
}
