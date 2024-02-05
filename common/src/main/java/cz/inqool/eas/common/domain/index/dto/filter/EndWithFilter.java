package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.validation.constraints.NotBlank;

import static cz.inqool.eas.common.domain.index.QueryUtils.escapeAll;
import static cz.inqool.eas.common.domain.index.QueryUtils.escapeAllButWildcard;

/**
 * Filter representing the 'ends with' filter condition on given text {@link FieldFilter#field}.
 */
@EqualsAndHashCode(callSuper = true)
public class EndWithFilter extends TextFilter<EndWithFilter> {

    EndWithFilter() {
        super(FilterOperation.END_WITH);
    }

    public EndWithFilter(@NotBlank String field, @NotBlank String value) {
        this(field, value, null);
    }

    public EndWithFilter(@NotBlank String field, @NotBlank String value, Modifier modifier) {
        super(FilterOperation.END_WITH, field, value, modifier);
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        IndexFieldLeafNode indexField = getIndexFieldLeafNode(indexedFields);

        String field = useFolding ? indexField.getFolded() : indexField.getElasticSearchPath();
        String foldedValue = foldValue(indexField);

        if (modifier != null) {
            QueryBuilder queryBuilder;
            switch (modifier) {
                case FUZZY: // fixme not working properly
                    queryBuilder = QueryBuilders.fuzzyQuery(indexField.getSearchable(), escapeAll(value))
                            .fuzziness(Fuzziness.AUTO)
                            .transpositions(true); // maybe remove for speed increase
                    break;
                case REGEXP:
                    queryBuilder = QueryBuilders.regexpQuery(field, ".*(" + foldedValue + ")");
                    break;
                case WILDCARD:
                    queryBuilder = QueryBuilders.wildcardQuery(field, "*" + escapeAllButWildcard(foldedValue));
                    break;
                default:
                    throw new RuntimeException("Unknown modifier");
            }
            return queryBuilder;
        }

        return QueryBuilders.wildcardQuery(field, "*" + escapeAll(foldedValue));
    }
}
