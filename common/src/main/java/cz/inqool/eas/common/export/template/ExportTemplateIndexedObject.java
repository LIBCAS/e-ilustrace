package cz.inqool.eas.common.export.template;

import cz.inqool.eas.common.dictionary.index.DictionaryIndexedObject;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Setter
@Document(indexName = "eas_export_template")
public class ExportTemplateIndexedObject extends DictionaryIndexedObject<ExportTemplate, ExportTemplate> {
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String label;

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference dataProvider;

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference designProvider;

    @Override
    public void toIndexedObject(ExportTemplate obj) {
        super.toIndexedObject(obj);

        this.label = obj.getLabel();
        this.dataProvider = LabeledReference.of(obj.getDataProvider());
        this.designProvider = LabeledReference.of(obj.getDesignProvider());
    }
}
