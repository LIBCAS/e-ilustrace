package cz.inqool.eas.common.domain.index.dto.filter;

import lombok.EqualsAndHashCode;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Filter representing the 'greater-than-or-equal' filter condition on given {@link FieldFilter#field}.
 */
@EqualsAndHashCode(callSuper = true)
public class GteFilter extends ComparisonFilter<GteFilter> {

    GteFilter() {
        super(FilterOperation.GTE);
    }

    public GteFilter(@NotBlank String field, @NotNull Number value) {
        this(field, value.toString());
    }

    public GteFilter(@NotBlank String field, @NotNull Number value, ShapeRelation relation) {
        this(field, value.toString(), relation);
    }

    public GteFilter(@NotBlank String field, @NotBlank String value) {
        this(field, value, null);
    }

    public GteFilter(@NotBlank String field, @NotBlank String value, ShapeRelation relation) {
        super(FilterOperation.GTE, field, value, relation);
    }


    @Override
    protected QueryBuilder applyOperation(RangeQueryBuilder query) {
        return query.gte(value);
    }
}
