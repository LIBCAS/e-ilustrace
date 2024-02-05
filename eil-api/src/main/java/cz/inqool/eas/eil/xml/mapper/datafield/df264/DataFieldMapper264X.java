package cz.inqool.eas.eil.xml.mapper.datafield.df264;

import cz.inqool.eas.eil.record.publication.PublicationEntry;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.eas.eil.xml.mapper.datafield.DataFieldMapper;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.IND_X;
import static cz.inqool.eas.eil.xml.mapper.Constants.TAG_264;
import static cz.inqool.eas.eil.xml.mapper.Utils.createPublicationEntry;

/**
 * {@link Illustration#printEntry}
 */
@Component
@Order(18)
public class DataFieldMapper264X implements DataFieldMapper264, DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        Illustration illustration = (Illustration) record;
        PublicationEntry printEntry = createPublicationEntry(field);
        illustration.setPrintEntry(printEntry);
    }

    @Override
    public String getTag() {
        return TAG_264;
    }

    @Override
    public boolean isBookMapper() {
        return false;
    }

    @Override
    public String getIndicator() {
        return IND_X;
    }
}
