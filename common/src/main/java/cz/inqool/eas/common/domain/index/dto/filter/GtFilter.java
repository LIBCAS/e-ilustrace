package cz.inqool.eas.common.domain.index.dto.filter;

import lombok.EqualsAndHashCode;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Filter representing the 'greater-than' filter condition on given {@link FieldFilter#field}.
 */
@EqualsAndHashCode(callSuper = true)
public class GtFilter extends ComparisonFilter<GtFilter> {

    GtFilter() {
        super(FilterOperation.GT);
    }

    public GtFilter(@NotBlank String field, @NotNull Number value) {
        this(field, value.toString());
    }

    public GtFilter(@NotBlank String field, @NotNull Number value, ShapeRelation relation) {
        this(field, value.toString(), relation);
    }

    public GtFilter(@NotBlank String field, @NotBlank String value) {
        this(field, value, null);
    }

    public GtFilter(@NotBlank String field, @NotBlank String value, ShapeRelation relation) {
        super(FilterOperation.GT, field, value, relation);
    }


    @Override
    protected QueryBuilder applyOperation(RangeQueryBuilder query) {
        return query.gt(value);
    }
}
