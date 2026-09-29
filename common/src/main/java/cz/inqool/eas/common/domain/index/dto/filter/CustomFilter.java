package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.validation.constraints.NotNull;

/**
 * Class representing custom filter where filter is stored as JSON string and in case its needed, {@link
 * Params#processCustomFilters(Class)} method is called for deserialize.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CustomFilter extends AbstractFilter {

    @NotNull
    private String customJsonModel;


    CustomFilter() {
        super(FilterOperation.CUSTOM);
    }

    public CustomFilter(@NotNull String customJsonModel) {
        super(FilterOperation.CUSTOM);
        this.customJsonModel = customJsonModel;
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        //return empty boolQuery because the custom filter is added explicitly in some cases
        return QueryBuilders.boolQuery();
    }
}
