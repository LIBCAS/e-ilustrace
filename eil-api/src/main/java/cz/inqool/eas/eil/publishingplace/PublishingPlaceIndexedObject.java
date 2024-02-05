package cz.inqool.eas.eil.publishingplace;

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
@org.springframework.data.elasticsearch.annotations.Document(indexName = "publishing_place")
@FieldNameConstants(innerTypeName = "IxFields")
public class PublishingPlaceIndexedObject extends DatedIndexedObject<PublishingPlace, PublishingPlaceIndexed> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String name;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String country;

    @Field(type = FieldType.Boolean)
    boolean fromBook;

    @Field(type = FieldType.Boolean)
    boolean fromIllustration;

    @Override
    public void toIndexedObject(PublishingPlaceIndexed obj) {
        super.toIndexedObject(obj);

        this.name = obj.getName();
        this.country = obj.getCountry();
        this.fromBook = obj.isFromBook();
        this.fromIllustration = obj.isFromIllustration();
    }

    public static PublishingPlaceIndexedObject of(PublishingPlaceIndexed obj) {
        if (obj == null) {
            return null;
        }
        PublishingPlaceIndexedObject indexed = new PublishingPlaceIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }

    public static PublishingPlaceIndexedObject of(PublishingPlace obj) {
        return of(PublishingPlaceIndexed.toView(obj));
    }
}
