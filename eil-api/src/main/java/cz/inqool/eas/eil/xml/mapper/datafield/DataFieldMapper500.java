package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.note.Note;
import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;

/**
 * {@link Record#notes}
 */
@Component
@Order(27)
public class DataFieldMapper500 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        Note note = new Note();

        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_3:
                    note.setTitle(subfield.getValue());
                    break;
                case CODE_A:
                    note.setText(subfield.getValue());
                    break;
            }
        });
        record.getNotes().add(note);
    }

    @Override
    public String getTag() {
        return TAG_500;
    }
}
