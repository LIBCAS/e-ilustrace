package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.illustration.Illustration;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.CODE_A;
import static cz.inqool.eas.eil.xml.mapper.Constants.TAG_591;

/**
 * {@link Illustration#defect}
 */
@Component
@Order(36)
public class DataFieldMapper591 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        Illustration illustration = (Illustration) record;

        field.getSubfield().forEach(subfield -> {
            if (subfield.getCode().equals(CODE_A)) {
                illustration.setDefect(subfield.getValue());
            }
        });
    }

    @Override
    public String getTag() {
        return TAG_591;
    }

    @Override
    public boolean isBookMapper() {
        return false;
    }
}
