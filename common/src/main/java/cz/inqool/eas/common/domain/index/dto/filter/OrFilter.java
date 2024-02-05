package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.QueryUtils;
import lombok.EqualsAndHashCode;
import org.elasticsearch.index.query.QueryBuilder;

import javax.validation.Valid;
import java.util.List;

/**
 * Filter representing logical disjunction (operation 'OR') between providen {@link LogicalFilter#filters}.
 */
@EqualsAndHashCode(callSuper = true)
public class OrFilter extends LogicalFilter {

    OrFilter() {
        super(FilterOperation.OR);
    }

    public OrFilter(@Valid Filter... filters) {
        this(List.of(filters));
    }

    public OrFilter(@Valid List<Filter> filters) {
        super(FilterOperation.OR, filters);
    }


    @Override
    protected QueryBuilder joinFilters(List<QueryBuilder> queries) {
        return QueryUtils.orQuery(queries);
    }
}
