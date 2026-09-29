package cz.inqool.eas.eil.domain;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.SORTING;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
public class EnumReference<E extends Enum<E>> {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String name;


    /**
     * Converts given {@code labeled} entity to labeled reference.
     *
     * @param value to be converted to labeled reference
     * @return new instance of labeled reference
     */
    public static <E extends Enum<E>> EnumReference<E> of(LabeledEnum<E> value) {
        if (value != null) {
            return new EnumReference<>(value.name(), value.getLabel());
        } else {
            return null;
        }
    }
}