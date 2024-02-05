package cz.inqool.eas.common.intl;

import cz.inqool.eas.common.dictionary.index.DictionaryIndexedObject;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.reference.LabeledReference.*;

@Getter
@Setter
@Document(indexName = "eas_translation")
public class TranslationIndexedObject extends DictionaryIndexedObject<Translation, Translation> {
    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference language;


    @Override
    public void toIndexedObject(Translation obj) {
        super.toIndexedObject(obj);

        this.language = of(obj.getLanguage());
    }
}
