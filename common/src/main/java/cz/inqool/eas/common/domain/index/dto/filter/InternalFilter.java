package cz.inqool.eas.common.domain.index.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.*;
import org.elasticsearch.index.query.QueryBuilder;

import javax.validation.constraints.NotNull;
import java.util.function.Function;

/**
 * Data transfer object for internal filter conditions. Used for additional complicated filters added on back-end.
 * <p>
 * Note that deserialization of this class is disabled and it can be used only on back-end.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonDeserialize // to override parent annotation
@EqualsAndHashCode
public class InternalFilter implements Filter {

    /**
     * Elastic search query for additional filtering on back-end.
     */
    @JsonIgnore
    @NotNull
    protected Function<IndexObjectFields, QueryBuilder> queryMapper;


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        return queryMapper.apply(indexedFields);
    }
}
