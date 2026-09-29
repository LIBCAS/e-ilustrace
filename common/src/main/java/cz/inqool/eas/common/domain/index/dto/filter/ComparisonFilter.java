package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Filter representing a range filter condition on given {@link FieldFilter#field} with supported ordering.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
abstract public class ComparisonFilter<FILTER extends ComparisonFilter<FILTER>> extends FieldValueFilter<FILTER> {

    /**
     * Modifies the operation execution behaviour
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.8/query-dsl-range-query.html#querying-range-fields">Range Filters</a>
     */
    private ShapeRelation relation = ShapeRelation.INTERSECTS;


    protected ComparisonFilter(@NotNull String operation) {
        super(operation);
    }

    protected ComparisonFilter(@NotNull String operation, @NotBlank String field, @NotBlank String value, ShapeRelation relation) {
        super(operation, field, value);
        this.relation = relation;
    }


    /**
     * Applies current instance operation to given query.
     *
     * @param query range query
     * @return build query
     */
    protected abstract QueryBuilder applyOperation(RangeQueryBuilder query);

    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        IndexFieldLeafNode indexField = getIndexFieldLeafNode(indexedFields);
        RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(indexField.getElasticSearchPath());
        if (relation != null) {
            queryBuilder.relation(relation.name());
        }
        return applyOperation(queryBuilder);
    }
}
