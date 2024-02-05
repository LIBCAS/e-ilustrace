package cz.inqool.eas.common.dao.simple.dictionary;

import cz.inqool.eas.common.dictionary.index.DictionaryIndexedObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

/**
 * Indexed object class for {@link SimpleDictionaryEntity}
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
@Getter
@Setter
@Document(indexName = "eas_simple_dictionary_entity")
@FieldNameConstants(innerTypeName = "IndexFields")
public class SimpleDictionaryIndexedObject extends DictionaryIndexedObject<SimpleDictionaryEntity, SimpleDictionaryEntity> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String otherUselessValue;

    @Override
    public void toIndexedObject(SimpleDictionaryEntity obj) {
        super.toIndexedObject(obj);

        this.otherUselessValue = obj.getOtherUselessValue();
    }
}
