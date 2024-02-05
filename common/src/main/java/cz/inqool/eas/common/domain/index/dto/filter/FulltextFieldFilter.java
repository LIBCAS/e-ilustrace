package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import cz.inqool.eas.common.exception.v2.IndexException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.validation.constraints.NotBlank;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.FIELD_NOT_FULLTEXT;

/**
 * Filter representing a filter condition on all fields using standard or biased search.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FulltextFieldFilter extends FieldValueFilter<FulltextFieldFilter> {

    FulltextFieldFilter() {
        super(FilterOperation.FTXF);
    }

    public FulltextFieldFilter(@NotBlank String field, @NotBlank String value) {
        super(FilterOperation.FTXF, field, value);
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        IndexFieldLeafNode indexField = getIndexFieldLeafNode(indexedFields);

        if (!indexField.isFulltext()) {
            throw new IndexException(FIELD_NOT_FULLTEXT)
                    .details(details -> details.property("field", field))
                    .debugInfo(info -> info.property("class", this.getClass()));
        }

        return QueryBuilders.multiMatchQuery(value)
                .field(indexField.getSearchable(), indexField.getBoost())
                .type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX)
                .slop(2);
    }
}
