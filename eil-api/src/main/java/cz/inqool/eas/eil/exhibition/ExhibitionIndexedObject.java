package cz.inqool.eas.eil.exhibition;

import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.eil.domain.EnumReference;
import cz.inqool.eas.eil.user.UserIndexedObject;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.SORTING;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Document(indexName = "exhibition")
@FieldNameConstants(innerTypeName = "IxFields")
public class ExhibitionIndexedObject extends DatedIndexedObject<Exhibition, ExhibitionIndexed> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String name;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String description;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    protected Instant published;

    @Field(type = FieldType.Nested, includeInParent = true)
    protected UserIndexedObject user;

    @Field(type = FieldType.Object, fielddata = true)
    protected EnumReference<Radio> radio;

    @Override
    public void toIndexedObject(ExhibitionIndexed obj) {
        super.toIndexedObject(obj);

        this.name = obj.getName();
        this.description = obj.getDescription();
        this.published = obj.getPublished();
        this.user = UserIndexedObject.of(obj.getUser());
        this.radio = EnumReference.of(obj.getRadio());
    }

    public static ExhibitionIndexedObject of(ExhibitionIndexed obj) {
        if (obj == null) {
            return null;
        }
        ExhibitionIndexedObject indexed = new ExhibitionIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }

    public static ExhibitionIndexedObject of(Exhibition obj) {
        return of(ExhibitionIndexed.toView(obj));
    }

}
