package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.reference.Reference;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;

/**
 * @see Record#references
 */
@Component
@Order(33)
public class DataFieldMapper510 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        Reference reference = new Reference();

        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    reference.setWorkTitle(subfield.getValue());
                    break;
                case CODE_C:
                    reference.setLocation(subfield.getValue());
                    break;
            }
        });
        record.getReferences().add(reference);
    }

    @Override
    public String getTag() {
        return TAG_510;
    }
}
