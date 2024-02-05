package cz.inqool.eas.eil.keyword;

import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.SORTING;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@org.springframework.data.elasticsearch.annotations.Document(indexName = "keyword")
@FieldNameConstants(innerTypeName = "IxFields")
public class KeywordIndexedObject extends DomainIndexedObject<Keyword, Keyword> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String label;

    @Override
    public void toIndexedObject(Keyword obj) {
        super.toIndexedObject(obj);

        this.label = obj.getLabel();
    }

    public static KeywordIndexedObject of(Keyword obj) {
        if (obj == null) {
            return null;
        }
        KeywordIndexedObject indexed = new KeywordIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }
}
