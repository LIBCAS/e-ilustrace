package cz.inqool.eas.eil.subject.place;

import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.illustration.Illustration;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@org.springframework.data.elasticsearch.annotations.Document(indexName = "subject_place")
@FieldNameConstants(innerTypeName = "IxFields")
public class SubjectPlaceIndexedObject extends DatedIndexedObject<SubjectPlace, SubjectPlaceIndexed> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String name;

    @Field(type = FieldType.Boolean)
    boolean fromBook;

    @Field(type = FieldType.Boolean)
    boolean fromIllustration;

    @Override
    public void toIndexedObject(SubjectPlaceIndexed obj) {
        super.toIndexedObject(obj);

        this.name = obj.getName();
        this.fromBook = obj.isFromBook();
        this.fromIllustration = obj.isFromIllustration();
    }

    public static SubjectPlaceIndexedObject of(SubjectPlaceIndexed obj) {
        if (obj == null) {
            return null;
        }
        SubjectPlaceIndexedObject indexed = new SubjectPlaceIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }

    public static SubjectPlaceIndexedObject of(SubjectPlace obj) {
        return of(SubjectPlaceIndexed.toView(obj));
    }
}
