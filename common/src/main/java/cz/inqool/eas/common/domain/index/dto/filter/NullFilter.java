package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import org.elasticsearch.index.query.QueryBuilder;

import javax.validation.constraints.NotBlank;

/**
 * Filter representing the 'is null' filter condition on given {@link FieldFilter#field}.
 *
 * @implNote not working properly with Nested fields, in that case use filter {@code NOT (NESTED (nestedPath, NOT_NULL (field)))}
 */
@EqualsAndHashCode(callSuper = true)
public class NullFilter extends FieldFilter<NullFilter> {

    NullFilter() {
        super(FilterOperation.IS_NULL);
    }

    public NullFilter(@NotBlank String field) {
        super(FilterOperation.IS_NULL, field);
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        return new NotFilter(new NotNullFilter(field)).toQueryBuilder(indexedFields);
    }
}
