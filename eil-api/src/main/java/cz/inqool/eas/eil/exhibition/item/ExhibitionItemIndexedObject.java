package cz.inqool.eas.eil.exhibition.item;

import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.eil.exhibition.ExhibitionIndexedObject;
import cz.inqool.eas.eil.record.RecordIndexedObject;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.SORTING;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Document(indexName = "exhibition_item")
@FieldNameConstants(innerTypeName = "IxFields")
public class ExhibitionItemIndexedObject extends DatedIndexedObject<ExhibitionItem, ExhibitionItem> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String description;

    @Field(type = FieldType.Nested, includeInParent = true)
    protected RecordIndexedObject illustration;

    @Field(type = FieldType.Nested, includeInParent = true)
    protected ExhibitionIndexedObject exhibition;


    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String name;

    @Field(type = FieldType.Keyword)
    protected String year;

    @Override
    public void toIndexedObject(ExhibitionItem obj) {
        super.toIndexedObject(obj);

        this.description = obj.getDescription();
        this.exhibition = ExhibitionIndexedObject.of(obj.getExhibition());
        this.illustration = RecordIndexedObject.of(obj.getIllustration());
        this.name = obj.getName();
        this.year = obj.getYear();
    }
}
