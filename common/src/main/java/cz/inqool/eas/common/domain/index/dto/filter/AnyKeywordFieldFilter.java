package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Filter representing a filter condition on match with any of all keyword fields using standard or biased search.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AnyKeywordFieldFilter extends ValueFilter {

    AnyKeywordFieldFilter() {
        super(FilterOperation.AKF);
    }

    @Builder
    public AnyKeywordFieldFilter(@NotBlank String value) {
        super(FilterOperation.AKF, value);
    }

    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        Map<String, Float> fields = indexedFields.values().stream()
                .filter(indexField -> indexField instanceof IndexFieldLeafNode)
                .map(indexField -> (IndexFieldLeafNode) indexField)
                .filter(indexFieldLeafNode -> indexFieldLeafNode.getType() == FieldType.Keyword)
                .collect(Collectors.toMap(IndexFieldLeafNode::getSearchable, IndexFieldLeafNode::getBoost));

        return QueryBuilders.multiMatchQuery(value)
                .fields(fields)
                .type(MultiMatchQueryBuilder.Type.PHRASE)
                .slop(2);
    }
}
