package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.publication.PublicationEntry;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.book.Book;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.TAG_260;
import static cz.inqool.eas.eil.xml.mapper.Utils.createPublicationEntry;

/**
 * {@link Book#publishingEntry}
 */
@Component
@Order(15)
public class DataFieldMapper260 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        Book book = (Book) record;
        PublicationEntry publishingEntry = createPublicationEntry(field);
        book.setPublishingEntry(publishingEntry);
    }

    @Override
    public String getTag() {
        return TAG_260;
    }

    @Override
    public boolean isIllustrationMapper() {
        return false;
    }
}
