package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.QueryBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter representing a logical filter operation between given {@link #filters}.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
abstract public class LogicalFilter extends AbstractFilter {

    /**
     * Nested filters
     */
    @Valid
    @NotEmpty
    protected List<@NotNull Filter> filters = new ArrayList<>();


    protected LogicalFilter(@NotNull String operation) {
        super(operation);
    }

    protected LogicalFilter(@NotNull String operation, @Valid List<Filter> filters) {
        super(operation);
        this.filters = filters;
    }


    /**
     * Joins {@link #filters} to one {@link QueryBuilder} condition.
     *
     * @param queries list of filters converted to QueryBuilder instances
     * @return joined queries into one filter condition
     */
    protected abstract QueryBuilder joinFilters(List<QueryBuilder> queries);

    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        List<QueryBuilder> queries = filters.stream()
                .map(filter -> filter.toQueryBuilder(indexedFields))
                .collect(Collectors.toList());

        return joinFilters(queries);
    }
}
