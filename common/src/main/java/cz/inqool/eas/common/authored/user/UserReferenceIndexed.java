package cz.inqool.eas.common.authored.user;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

/**
 * Indexed reference to User.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
public class UserReferenceIndexed {

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
     * Converts given {@link UserReference} object to labeled reference.
     *
     * @param obj value to be converted to labeled reference
     * @return new instance of labeled reference
     */
    public static UserReferenceIndexed of(UserReference obj) {
        if (obj != null) {
            return new UserReferenceIndexed(obj.getId(), obj.getName());
        } else {
            return null;
        }
    }
}
