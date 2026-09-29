package cz.inqool.eas.common.schedule.job;

import cz.inqool.eas.common.dictionary.index.DictionaryIndexedObject;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.TEXT_SHORT_KEYWORD;

@Getter
@Setter
@Document(indexName = "eas_schedule_job")
public class JobIndexedObject extends DictionaryIndexedObject<Job, Job> {

    @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD, fielddata = true)
    protected String timer;

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference scriptType;

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference logAction;

    @Field(type = FieldType.Boolean)
    protected boolean useTransaction;

    @Override
    public void toIndexedObject(Job obj) {
        super.toIndexedObject(obj);

        this.timer = obj.getTimer();
        this.scriptType = LabeledReference.of(obj.getScriptType());
        this.logAction = LabeledReference.of(obj.getLogAction());
        this.useTransaction = obj.isUseTransaction();
    }
}
