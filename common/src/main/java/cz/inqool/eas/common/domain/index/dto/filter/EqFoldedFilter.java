package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Filter representing the 'equals' filter with folding condition on given {@link FieldFilter#field}.
 */
@EqualsAndHashCode(callSuper = true)
public class EqFoldedFilter extends FieldValueFilter<EqFoldedFilter> {

    EqFoldedFilter() {
        super(FilterOperation.EQF);
    }

    public EqFoldedFilter(@NotBlank String field, @NotNull Enum<?> value) {
        this(field, value.name());
    }

    public EqFoldedFilter(@NotBlank String field, @NotBlank String value) {
        super(FilterOperation.EQ, field, value);
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        IndexFieldLeafNode indexField = getIndexFieldLeafNode(indexedFields);

        return QueryBuilders.matchQuery(indexField.getFolded(), value)
                .operator(Operator.AND);
    }
}
