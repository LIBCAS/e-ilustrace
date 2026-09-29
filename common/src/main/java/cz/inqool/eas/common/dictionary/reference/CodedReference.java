package cz.inqool.eas.common.dictionary.reference;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.function.Function;

/**
 * Reference to another Dictionary Index object.
 * <p>
 * Objects in Index can not be connected like JPA entities. Therefore we store only the referencing {@link
 * CodedReference#id}, {@link CodedReference#name} and {@link CodedReference#code} attribute, which corresponds to label of that entity.
 * <p>
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
public class CodedReference extends LabeledReference {

    @Field(type = FieldType.Keyword)
    protected String code;


    public CodedReference() {
    }

    public CodedReference(String id, String name, String code) {
        super(id, name);
        this.code = code;
    }

    /**
     * Converts given {@code labeled} entity to labeled reference.
     *
     * @param entity value to be converted to labeled reference
     * @return new instance of labeled reference
     */
    public static CodedReference of(Coded entity) {
        if (entity != null) {
            return new CodedReference(entity.getId(), entity.getLabel(), entity.getCode());
        } else {
            return null;
        }
    }

    /**
     * Converts given {@code obj} entity to coded reference using provided name mapper.
     * The obj needs to be of type DomainObject.
     *
     * @param obj value to be converted to labeled reference
     * @param labelMapper Mapper from Object to name
     * @param codeMapper Mapper from Object to code
     * @return new instance of labeled reference
     */
    public static <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> CodedReference of(PROJECTED obj, Function<PROJECTED, String> labelMapper, Function<PROJECTED, String> codeMapper) {
        if (obj != null) {
            return new CodedReference(obj.getId(), labelMapper.apply(obj), codeMapper.apply(obj));
        } else {
            return null;
        }
    }
}
