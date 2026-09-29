package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;
import gov.loc.marc21.slim.SubfieldatafieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.CODE_A;
import static cz.inqool.eas.eil.xml.mapper.Constants.TAG_246;

/**
 * {@link Record#variantTitles}
 */
@Component
@Order(12)
public class DataFieldMapper246 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        field.getSubfield().stream()
                .filter(s -> s.getCode().equals(CODE_A))
                .findFirst()
                .map(SubfieldatafieldType::getValue)
                .ifPresent(variantTitle -> record.getVariantTitles().add(variantTitle));
    }

    @Override
    public String getTag() {
        return TAG_246;
    }
}
