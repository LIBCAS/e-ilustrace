package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.owner.Owner;
import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;

/**
 * {@link Record#owners}
 */
@Component
@Order(66)
public class DataFieldMapper910 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        Owner owner = new Owner();
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    owner.setName(subfield.getValue());
                    break;
                case CODE_B:
                    owner.setSignature(subfield.getValue());
                    break;
            }
        });
        record.getOwners().add(owner);
    }

    @Override
    public String getTag() {
        return TAG_910;
    }
}
