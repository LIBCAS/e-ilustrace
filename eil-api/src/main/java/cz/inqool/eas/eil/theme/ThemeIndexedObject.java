package cz.inqool.eas.eil.theme;

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
@org.springframework.data.elasticsearch.annotations.Document(indexName = "theme")
@FieldNameConstants(innerTypeName = "IxFields")
public class ThemeIndexedObject extends DatedIndexedObject<Theme, Theme> {

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
    public void toIndexedObject(Theme obj) {
        super.toIndexedObject(obj);

        this.name = obj.getName();
    }

    public static ThemeIndexedObject of(Theme obj) {
        if (obj == null) {
            return null;
        }
        ThemeIndexedObject indexed = new ThemeIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }
}
