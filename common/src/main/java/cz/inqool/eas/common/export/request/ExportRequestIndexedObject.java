package cz.inqool.eas.common.export.request;

import cz.inqool.eas.common.authored.index.AuthoredIndexedObject;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Getter
@Setter
@Document(indexName = "eas_export_request")
public class ExportRequestIndexedObject extends AuthoredIndexedObject<ExportRequest, ExportRequest> {
    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference template;

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference type;

    @Field(type = FieldType.Integer)
    protected Integer priority;

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference state;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    protected Instant processingStart;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    protected Instant processingEnd;

    @Field(type = FieldType.Boolean)
    protected boolean systemRequest;

    @Override
    public void toIndexedObject(ExportRequest obj) {
        super.toIndexedObject(obj);

        this.template = LabeledReference.of(obj.getTemplate());
        this.type = LabeledReference.of(obj.getType());
        this.priority = obj.getPriority();
        this.state = LabeledReference.of(obj.getState());
        this.processingStart = obj.getProcessingStart();
        this.processingEnd = obj.getProcessingEnd();
        this.systemRequest = obj.isSystemRequest();
    }
}
