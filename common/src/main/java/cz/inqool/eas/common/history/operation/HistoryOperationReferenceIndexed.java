package cz.inqool.eas.common.history.operation;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

/**
 * Indexed reference to History operation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HistoryOperationReferenceIndexed {

    @Id
    @Field(type = FieldType.Keyword)
    protected String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String name;

    /**
     * Converts given {@link HistoryOperationReference} object to labeled reference.
     *
     * @param obj value to be converted to labeled reference
     * @return new instance of labeled reference
     */
    public static HistoryOperationReferenceIndexed of(HistoryOperationReference obj) {
        if (obj != null) {
            return new HistoryOperationReferenceIndexed(obj.getId(), obj.getName());
        } else {
            return null;
        }
    }
}
