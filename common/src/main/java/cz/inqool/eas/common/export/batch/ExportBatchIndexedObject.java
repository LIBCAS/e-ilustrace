package cz.inqool.eas.common.export.batch;

import cz.inqool.eas.common.authored.index.AuthoredIndexedObject;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.SORTING;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Setter
@Document(indexName = "eas_export_batch")
public class ExportBatchIndexedObject extends AuthoredIndexedObject<ExportBatch, ExportBatch> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String name;

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference state;

    @Override
    public void toIndexedObject(ExportBatch obj) {
        super.toIndexedObject(obj);
        this.name = obj.getName();
        this.state = LabeledReference.of(obj.getState());
    }
}
