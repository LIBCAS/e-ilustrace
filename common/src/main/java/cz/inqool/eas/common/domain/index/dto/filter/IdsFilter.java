package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Filter representing filter to return items with matching IDs
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class IdsFilter extends AbstractFilter {

    @NotNull
    private Set<@NotBlank String> ids;


    IdsFilter() {
        super(FilterOperation.IDS);
    }

    public IdsFilter(@NotNull String... ids) {
        super(FilterOperation.IDS);
        this.ids = Set.of(ids);
    }

    public IdsFilter(@NotNull Set<@NotBlank String> ids) {
        super(FilterOperation.IDS);
        this.ids = ids;
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        return QueryBuilders.idsQuery().addIds(ids.toArray(new String[0]));
    }
}
