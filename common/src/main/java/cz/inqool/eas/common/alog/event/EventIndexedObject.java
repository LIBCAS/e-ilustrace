package cz.inqool.eas.common.alog.event;

import cz.inqool.eas.common.authored.index.AuthoredIndexedObject;
import cz.inqool.eas.common.authored.user.UserReferenceIndexed;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import cz.inqool.eas.common.module.ModuleReferenceIndexed;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Setter
@Document(indexName = "eas_alog_event")
public class EventIndexedObject extends AuthoredIndexedObject<Event, Event> {
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    //@InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String message;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String source;

    @Field(type = FieldType.Object)
    protected ModuleReferenceIndexed module;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String ipAddress;

    @Field(type = FieldType.Object)
    protected UserReferenceIndexed user;

    @Field(type = FieldType.Object)
    private LabeledReference sourceType;

    @Field(type = FieldType.Object)
    private LabeledReference severity;

    @Field(type = FieldType.Boolean)
    private boolean syslog;

    @Override
    public void toIndexedObject(Event obj) {
        super.toIndexedObject(obj);

        this.sourceType = LabeledReference.of(obj.getSourceType());
        this.source = obj.getSource();
        this.module = ModuleReferenceIndexed.of(obj.getModule());
        this.ipAddress = obj.getIpAddress();
        this.severity = LabeledReference.of(obj.getSeverity());
        this.message = obj.getMessage();
        this.syslog = obj.isSyslog();
        this.user = UserReferenceIndexed.of(obj.getUser());
    }
}
