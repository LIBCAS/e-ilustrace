package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

/**
 * Because the existing range filter is inferior in every single way.
 * If you have a doc containing both 1900 and 2000 as values
 * it matches single value range filters for x > 1950 AND x < 1960
 *
 * @author Lukas Jane (inQool) 25.01.2021.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class RangeFilter extends FieldFilter<RangeFilter> {

    protected String gte;
    protected String gt;
    protected String lte;
    protected String lt;

    protected ShapeRelation relation = ShapeRelation.INTERSECTS;

    RangeFilter() {
        super(FilterOperation.RANGE);
    }

    @Builder
    public RangeFilter(String field, String gte, String gt, String lte, String lt, ShapeRelation relation) {
        super(FilterOperation.RANGE, field);
        this.gte = gte;
        this.gt = gt;
        this.lte = lte;
        this.lt = lt;
        this.relation = relation;
    }

    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        IndexFieldLeafNode indexField = getIndexFieldLeafNode(indexedFields);
        RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(indexField.getElasticSearchPath());
        if (relation != null) {
            queryBuilder.relation(relation.name());
        }
        queryBuilder.gt(gt);
        queryBuilder.gte(gte);
        queryBuilder.lt(lt);
        queryBuilder.lte(lte);
        return queryBuilder;
    }
}
