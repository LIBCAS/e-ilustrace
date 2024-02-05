package cz.inqool.eas.eil.xml.mapper.datafield.df264;

import cz.inqool.eas.eil.record.publication.PublicationEntry;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.eas.eil.xml.mapper.datafield.DataFieldMapper;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.IND_3;
import static cz.inqool.eas.eil.xml.mapper.Constants.TAG_264;
import static cz.inqool.eas.eil.xml.mapper.Utils.createPublicationEntry;

/**
 * {@link Illustration#printingPlateEntry}
 */
@Component
@Order(21)
public class DataFieldMapper2643 implements DataFieldMapper264, DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        Illustration illustration = (Illustration) record;
        PublicationEntry printingPlateEntry = createPublicationEntry(field);
        illustration.setPrintingPlateEntry(printingPlateEntry);
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
        return IND_3;
    }
}
