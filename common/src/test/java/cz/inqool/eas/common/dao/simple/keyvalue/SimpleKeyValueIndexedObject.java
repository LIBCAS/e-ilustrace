package cz.inqool.eas.common.dao.simple.keyvalue;


import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Setter
@Document(indexName = "eas_simple_key_value")
@FieldNameConstants(innerTypeName = "IndexFields")
public class SimpleKeyValueIndexedObject extends DomainIndexedObject<SimpleKeyValueEntity, SimpleKeyValueEntity> {

    @Field(type = FieldType.Keyword)
    private String key;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String value;

    @Override
    public void toIndexedObject(SimpleKeyValueEntity obj) {
        super.toIndexedObject(obj);
        this.key = obj.getKey();
        this.value = obj.getValue();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
