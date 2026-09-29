package cz.inqool.eas.eil.subject.person;

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
@org.springframework.data.elasticsearch.annotations.Document(indexName = "subject_person")
@FieldNameConstants(innerTypeName = "IxFields")
public class SubjectPersonIndexedObject extends DatedIndexedObject<SubjectPerson, SubjectPersonIndexed> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String fullName;

    @Field(type = FieldType.Keyword)
    private String birthYear;

    @Field(type = FieldType.Keyword)
    private String deathYear;

    @Field(type = FieldType.Boolean)
    boolean fromBook;

    @Field(type = FieldType.Boolean)
    boolean fromIllustration;

    @Override
    public void toIndexedObject(SubjectPersonIndexed obj) {
        super.toIndexedObject(obj);

        this.fullName = obj.getFullName();
        this.birthYear = obj.getBirthYear();
        this.deathYear = obj.getDeathYear();
        this.fromBook = obj.isFromBook();
        this.fromIllustration = obj.isFromIllustration();
    }

    public static SubjectPersonIndexedObject of(SubjectPersonIndexed obj) {
        if (obj == null) {
            return null;
        }
        SubjectPersonIndexedObject indexed = new SubjectPersonIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }

    public static SubjectPersonIndexedObject of(SubjectPerson obj) {
        return of(SubjectPersonIndexed.toView(obj));
    }
}
