package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Filter representing nested filter (to be able to query nested object fields).
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class NestedFilter extends AbstractFilter {

    /**
     * Nested object path
     */
    @NotBlank
    private String path;

    /**
     * Query filter
     */
    @Valid
    @NotNull
    private Filter filter;

    /**
     * Score mode
     */
    private ScoreMode scoreMode = ScoreMode.None;


    NestedFilter() {
        super(FilterOperation.NESTED);
    }

    public NestedFilter(@NotBlank String path, @Valid Filter filter) {
        super(FilterOperation.NESTED);
        this.path = path;
        this.filter = filter;
    }

    public NestedFilter(@NotBlank String path, @Valid Filter filter, @NotNull ScoreMode scoreMode) {
        super(FilterOperation.NESTED);
        this.path = path;
        this.filter = filter;
        this.scoreMode = scoreMode;
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        return QueryBuilders.nestedQuery(path, filter.toQueryBuilder(indexedFields), scoreMode);
    }
}
