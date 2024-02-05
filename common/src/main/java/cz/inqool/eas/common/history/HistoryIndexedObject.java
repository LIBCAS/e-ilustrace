package cz.inqool.eas.common.history;

import cz.inqool.eas.common.authored.index.AuthoredIndexedObject;
import cz.inqool.eas.common.history.operation.HistoryOperationReferenceIndexed;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Setter
@Document(indexName = "eas_history")
public class HistoryIndexedObject extends AuthoredIndexedObject<History, History> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String description;

    @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD, fielddata = true)
    protected String entityId;

    @Field(type = FieldType.Object, fielddata = true)
    protected HistoryOperationReferenceIndexed operation;

    @Override
    public void toIndexedObject(History obj) {
        super.toIndexedObject(obj);

        this.description = obj.getDescription();
        this.entityId = obj.getEntityId();
        this.operation = HistoryOperationReferenceIndexed.of(obj.getOperation());
    }
}
