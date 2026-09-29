package cz.inqool.eas.common.domain.index.reference;

import cz.inqool.eas.common.domain.Domain;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.util.function.Function;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

/**
 * Reference to another Index object.
 * <p>
 * Objects in Index can not be connected like JPA entities. Therefore we store only the referencing {@link
 * LabeledReference#id} and {@link LabeledReference#name} attribute, which corresponds to label of that entity.
 * <p>
 * If used as reference to {@link Enum} then, the {@link LabeledReference#id} becomes the {@link Enum#name()} and {@link
 * LabeledReference#name} should be set to something the user see and filter on.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
public class LabeledReference {

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
     * Converts given {@code labeled} entity to labeled reference.
     *
     * @param entity value to be converted to labeled reference
     * @return new instance of labeled reference
     */
    public static LabeledReference of(Labeled entity) {
        if (entity != null) {
            return new LabeledReference(entity.getId(), entity.getLabel());
        } else {
            return null;
        }
    }

    /**
     * Converts given {@code obj} entity to labeled reference using provided name mapper.
     * The obj needs to be of type DomainObject.
     *
     * @param obj value to be converted to labeled reference
     * @param labelMapper Mapper from Object to name
     * @return new instance of labeled reference
     */
    public static <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> LabeledReference of(PROJECTED obj, Function<PROJECTED, String> labelMapper) {
        if (obj != null) {
            return new LabeledReference(obj.getId(), labelMapper.apply(obj));
        } else {
            return null;
        }
    }
}
