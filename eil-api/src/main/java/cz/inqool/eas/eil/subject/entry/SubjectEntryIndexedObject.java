package cz.inqool.eas.eil.subject.entry;

import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@org.springframework.data.elasticsearch.annotations.Document(indexName = "subject_heading")
@FieldNameConstants(innerTypeName = "IxFields")
public class SubjectEntryIndexedObject extends DatedIndexedObject<SubjectEntry, SubjectEntryIndexed> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String label;

    @Field(type = FieldType.Boolean)
    boolean fromBook;

    @Field(type = FieldType.Boolean)
    boolean fromIllustration;

    @Override
    public void toIndexedObject(SubjectEntryIndexed obj) {
        super.toIndexedObject(obj);

        this.label = obj.getLabel();
        this.fromBook = obj.isFromBook();
        this.fromIllustration = obj.isFromIllustration();
    }

    public static SubjectEntryIndexedObject of(SubjectEntryIndexed obj) {
        if (obj == null) {
            return null;
        }
        SubjectEntryIndexedObject indexed = new SubjectEntryIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }

    public static SubjectEntryIndexedObject of(SubjectEntry obj) {
        return of(SubjectEntryIndexed.toView(obj));
    }
}
