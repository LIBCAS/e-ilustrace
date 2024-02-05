package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.QueryUtils;
import lombok.EqualsAndHashCode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.validation.Valid;
import java.util.List;

/**
 * Filter representing logical negation (operation 'NOT') between providen {@link LogicalFilter#filters}.
 */
@EqualsAndHashCode(callSuper = true)
public class NotFilter extends LogicalFilter {

    NotFilter() {
        super(FilterOperation.NOT);
    }

    public NotFilter(@Valid Filter... filters) {
        this(List.of(filters));
    }

    public NotFilter(@Valid List<Filter> filters) {
        super(FilterOperation.NOT, filters);
    }


    @Override
    protected QueryBuilder joinFilters(List<QueryBuilder> queries) {
        switch (queries.size()) {
            case 0:
                return QueryBuilders.boolQuery();
            case 1:
                return QueryBuilders.boolQuery().mustNot(queries.get(0));
            default:
                return QueryBuilders.boolQuery().mustNot(QueryUtils.andQuery(queries));
        }
    }
}
