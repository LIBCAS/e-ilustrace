package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;
import gov.loc.marc21.slim.SubfieldatafieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.CODE_A;
import static cz.inqool.eas.eil.xml.mapper.Constants.TAG_245;

/**
 * {@link Record#title}
 */
@Component
@Order(9)
public class DataFieldMapper245 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        String title = field.getSubfield().stream()
                .filter(s -> s.getCode().equals(CODE_A))
                .findFirst()
                .map(SubfieldatafieldType::getValue)
                .orElse(null);

        record.setTitle(title);
    }

    @Override
    public String getTag() {
        return TAG_245;
    }
}
