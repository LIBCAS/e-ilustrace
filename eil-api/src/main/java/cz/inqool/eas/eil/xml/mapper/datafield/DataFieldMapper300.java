package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;

/**
 * {@link Record#physicalDescription}
 * {@link Record#technique}
 * {@link Record#dimensions}
 */
@Component
@Order(24)
public class DataFieldMapper300 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    record.setPhysicalDescription(subfield.getValue());
                    break;
                case CODE_B:
                    record.setTechnique(subfield.getValue());
                    break;
                case CODE_C:
                    record.setDimensions(subfield.getValue());
                    break;
            }
        });
    }

    @Override
    public String getTag() {
        return TAG_300;
    }
}
