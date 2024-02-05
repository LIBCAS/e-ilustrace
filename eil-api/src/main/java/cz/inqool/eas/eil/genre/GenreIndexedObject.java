package cz.inqool.eas.eil.genre;

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
@org.springframework.data.elasticsearch.annotations.Document(indexName = "genre")
@FieldNameConstants(innerTypeName = "IxFields")
public class GenreIndexedObject extends DatedIndexedObject<Genre, Genre> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String name;

    @Override
    public void toIndexedObject(Genre obj) {
        super.toIndexedObject(obj);

        this.name = obj.getName();
    }

    public static GenreIndexedObject of(Genre obj) {
        if (obj == null) {
            return null;
        }
        GenreIndexedObject indexed = new GenreIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }
}
