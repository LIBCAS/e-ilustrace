package cz.inqool.eas.common.reporting.report;

import cz.inqool.eas.common.authored.index.AuthoredIndexedObject;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Document(indexName = "eas_reporting_report")
@FieldNameConstants
public class ReportIndexedObject extends AuthoredIndexedObject<Report, Report> {

    @Field(type = FieldType.Text, fielddata = true)
    protected String definitionId;

    @Override
    public void toIndexedObject(Report obj) {
        super.toIndexedObject(obj);

        this.definitionId = obj.getDefinitionId();
    }
}
