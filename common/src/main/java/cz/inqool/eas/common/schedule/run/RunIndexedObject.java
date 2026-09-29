package cz.inqool.eas.common.schedule.run;

import cz.inqool.eas.common.dated.index.DatedIndexedObject;
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
@Document(indexName = "eas_schedule_run")
public class RunIndexedObject extends DatedIndexedObject<Run, Run> {

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference job;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    protected Instant startTime;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    protected Instant endTime;

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference state;

    @Override
    public void toIndexedObject(Run obj) {
        super.toIndexedObject(obj);

        this.job = LabeledReference.of(obj.getJob());
        this.startTime = obj.getStartTime();
        this.endTime = obj.getEndTime();
        this.state = LabeledReference.of(obj.getState());
    }
}
