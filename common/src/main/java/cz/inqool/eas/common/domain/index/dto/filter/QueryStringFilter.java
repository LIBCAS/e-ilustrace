package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.lang.NonNull;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.AbstractQueryBuilder.DEFAULT_BOOST;

/**
 * Filters items matching specified query
 *
 * @see <a
 * href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html">Query string
 * query</a>
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class QueryStringFilter extends AbstractFilter {

    private static final String OPERATION = FilterOperation.QUERY_STRING;


    /**
     * Query string you wish to parse and use for search
     */
    @NotNull
    private String value;

    /**
     * Default boolean logic used to interpret text in the query string if no operators are specified
     */
    @NotNull
    private Operator defaultOperator = Operator.OR;

    /**
     * Array of fields to search. Supports wildcards (*)
     */
    @Nullable
    private Map<@NotNull String, @NotNull Float> fields;


    QueryStringFilter() {
        super(OPERATION);
    }

    public QueryStringFilter(@NonNull String value) {
        super(OPERATION);
        this.value = value;
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        if (fields == null) {
            fields = indexedFields.values().stream()
                    .filter(node -> node instanceof IndexFieldLeafNode)
                    .map(node -> (IndexFieldLeafNode) node)
                    .filter(IndexFieldLeafNode::isIndexed)
                    .filter(IndexFieldLeafNode::isFulltext)
                    .map(IndexFieldLeafNode::getSearchable)
                    .collect(Collectors.toMap(Function.identity(), path -> DEFAULT_BOOST));
        } else {
            fields.forEach((field, boost) -> {
                if (!"*".equals(field)) {
                    // check if given field is mapped
                    indexedFields.get(field, IndexFieldLeafNode.class);
                }
            });
        }

        return QueryBuilders.queryStringQuery(value)
                .fields(fields)
                .allowLeadingWildcard(true)
                .autoGenerateSynonymsPhraseQuery(true)
                .defaultOperator(defaultOperator)
                .fuzziness(Fuzziness.AUTO);
    }

    /**
     * @see #defaultOperator
     */
    public QueryStringFilter defaultOperator(@NonNull Operator defaultOperator) {
        this.defaultOperator = defaultOperator;
        return this;
    }

    /**
     * @see #fields
     */
    public QueryStringFilter addField(@NonNull String field) {
        return addField(field, DEFAULT_BOOST);
    }

    /**
     * @see #fields
     */
    public QueryStringFilter addField(@NonNull String field, @NonNull Float boost) {
        if (fields == null) {
            fields = new TreeMap<>();
        }

        fields.put(field, boost);
        return this;
    }
}
