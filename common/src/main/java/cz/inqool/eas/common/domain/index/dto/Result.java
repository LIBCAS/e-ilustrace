package cz.inqool.eas.common.domain.index.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.inqool.eas.common.domain.index.dto.aggregation.AggregationResult;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.domain.index.dto.params.SearchAfterDeserializer;
import cz.inqool.eas.common.domain.index.dto.params.SearchAfterSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 *POJO for returning instances with total count.
 *
 * @param <T> Type of instances to hold.
 */
@Schema(name="Result", description="POJO that represents the collection of items with total count.")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * List of instances.
     */
    private List<T> items;

    /**
     * Total count of instances in store (possibly satisfying the specified {@link Params} filters.
     */
    private Long count;

    /**
     * Infinity scroll support. Contains last result's sorting field values (in correct order). Can be used in {@link
     * Params#getSearchAfter()}
     */
    @JsonSerialize(using = SearchAfterSerializer.class)
    @JsonDeserialize(using = SearchAfterDeserializer.class)
    private Object[] searchAfter;

    /**
     * Aggregation results, if any were requested. Mapped under name that was specified in query.
     */
    protected Map<String, List<? extends AggregationResult>> aggregations;
}
