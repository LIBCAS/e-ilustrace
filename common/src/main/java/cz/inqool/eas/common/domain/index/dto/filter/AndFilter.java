package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.QueryUtils;
import lombok.EqualsAndHashCode;
import org.elasticsearch.index.query.QueryBuilder;

import javax.validation.Valid;
import java.util.List;

/**
 * Filter representing logical conjunction (operation 'AND') between providen {@link LogicalFilter#filters}.
 */
@EqualsAndHashCode(callSuper = true)
public class AndFilter extends LogicalFilter {

    AndFilter() {
        super(FilterOperation.AND);
    }

    public AndFilter(@Valid Filter... filters) {
        this(List.of(filters));
    }

    public AndFilter(@Valid List<Filter> filters) {
        super(FilterOperation.AND, filters);
    }


    @Override
    protected QueryBuilder joinFilters(List<QueryBuilder> queries) {
        return QueryUtils.andQuery(queries);
    }
}
