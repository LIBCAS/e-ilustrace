package cz.inqool.eas.common.action;

import cz.inqool.eas.common.dictionary.index.DictionaryIndexedObject;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName = "eas_action")
public class ActionIndexedObject extends DictionaryIndexedObject<Action, Action> {

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference scriptType;

    @Field(type = FieldType.Boolean)
    protected boolean useTransaction;

    @Override
    public void toIndexedObject(Action obj) {
        super.toIndexedObject(obj);

        this.scriptType = LabeledReference.of(obj.getScriptType());
        this.useTransaction = obj.isUseTransaction();
    }
}
