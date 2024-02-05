package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.link.Link;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.eas.eil.record.link.LinkEnum;
import cz.inqool.eas.eil.xml.MarcProcessor;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Predicate;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;

/**
 * {@link Record#links}
 * Related also to {@link Illustration#illustrationScan}, {@link Illustration#pageScan} and {@link Book#frontPageScan}
 * @see MarcProcessor#handleImageDownload(Record) 
 */
@Component
@Order(63)
public class DataFieldMapper856 implements DataFieldMapper {

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        Link link = new Link();
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_U:
                    link.setUrl(subfield.getValue());
                    break;
                case CODE_Y:
                    link.setDescription(subfield.getValue());
                    break;
            }
        });

        String description = link.getDescription().trim().toLowerCase();
        if (Arrays.stream(LinkEnum.values())
                .map(LinkEnum::getLabel)
                .noneMatch(description::startsWith))
            return;
        if (record.getLinks().stream().map(Link::getUrl).noneMatch(Predicate.isEqual(link.getUrl()))) {
            link.setRecord(record);
            record.getLinks().add(link);
        }
    }

    @Override
    public String getTag() {
        return TAG_856;
    }
}
