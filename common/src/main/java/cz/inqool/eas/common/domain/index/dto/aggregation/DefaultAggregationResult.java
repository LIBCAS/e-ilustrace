package cz.inqool.eas.common.domain.index.dto.aggregation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * DTO holding aggregation results parsed from ElasticSearch
 */
@Getter
@Setter
public class DefaultAggregationResult implements AggregationResult {

    private String key;
    private String value;
    private Map<String, List<? extends AggregationResult>> aggregations;
}
