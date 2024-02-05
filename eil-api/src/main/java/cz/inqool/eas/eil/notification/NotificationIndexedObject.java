package cz.inqool.eas.eil.notification;

import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.common.domain.index.field.Boost;
import cz.inqool.eas.eil.domain.EnumReference;
import cz.inqool.eas.eil.notification.event.NotificationEvent;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.SORTING;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Document(indexName = "eil_notification")
@FieldNameConstants(innerTypeName = "IxFields")
public class NotificationIndexedObject extends DatedIndexedObject<Notification, Notification> {

    @Field(type = FieldType.Object, fielddata = true)
    private EnumReference<NotificationType> type;

    @Boost(0)
    @Field(type = FieldType.Object, fielddata = true)
    private EnumReference<NotificationEvent> event;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD, searchAnalyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING, searchAnalyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING, searchAnalyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, searchAnalyzer = SORTING, fielddata = true)
            }
    )
    private String subject;

    @Override
    public void toIndexedObject(Notification obj) {
        super.toIndexedObject(obj);

        this.type = EnumReference.of(obj.getType());
        this.event = EnumReference.of(obj.getEvent());
        this.subject = obj.getSubject();
    }
}
