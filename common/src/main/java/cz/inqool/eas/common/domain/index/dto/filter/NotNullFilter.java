package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexFieldNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.validation.constraints.NotBlank;

/**
 * Filter representing the 'not-null' filter condition on given {@link FieldFilter#field}.
 */
@EqualsAndHashCode(callSuper = true)
public class NotNullFilter extends FieldFilter<NotNullFilter> {

    NotNullFilter() {
        super(FilterOperation.NOT_NULL);
    }

    public NotNullFilter(@NotBlank String field) {
        super(FilterOperation.NOT_NULL, field);
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        IndexFieldNode indexField = indexedFields.get(field, IndexFieldNode.class);

        return QueryBuilders.existsQuery(indexField.getElasticSearchPath());
    }
}
