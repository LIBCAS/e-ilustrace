package cz.inqool.eas.eil.notification.template;

import cz.inqool.eas.common.authored.index.AuthoredIndexedObject;
import cz.inqool.eas.common.domain.index.field.Boost;
import cz.inqool.eas.eil.domain.EnumReference;
import cz.inqool.eas.eil.notification.event.NotificationEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.SORTING;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.SORT;

@Getter
@Setter
@Document(indexName = "eil_notification_template")
@FieldNameConstants(innerTypeName = "IxFields")
public class NotificationTemplateIndexedObject extends AuthoredIndexedObject<NotificationTemplate, NotificationTemplate> {

    @Field(type = FieldType.Object, fielddata = true)
    private EnumReference<NotificationTemplateType> type;

    @Boost(10)
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String name;

    @Boost
    @Field(type = FieldType.Object, fielddata = true)
    private EnumReference<NotificationEvent> event;

    @Boost(2)
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String subject;

    @Field(type = FieldType.Boolean)
    private boolean active;


    @Override
    public void toIndexedObject(NotificationTemplate obj) {
        super.toIndexedObject(obj);

        this.type = EnumReference.of(obj.getType());
        this.name = obj.getName();
        this.event = EnumReference.of(obj.getEvent());
        this.subject = obj.getSubject();
        this.active = obj.isActive();
    }
}
