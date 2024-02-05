package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.CODE_T;
import static cz.inqool.eas.eil.xml.mapper.Constants.TAG_505;

/**
 * {@link Record#contentNotes}
 */
@Component
@Order(30)
public class DataFieldMapper505 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        field.getSubfield().forEach(subfield -> {
            if (subfield.getCode().equals(CODE_T)) {
                record.getContentNotes().add(subfield.getValue());
            }
        });
    }

    @Override
    public String getTag() {
        return TAG_505;
    }
}
