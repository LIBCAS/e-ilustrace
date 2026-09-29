package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.publishingplace.PublishingPlace;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceRepository;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.book.Book;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;
import static cz.inqool.eas.eil.xml.mapper.Utils.getNameFromDataField;

/**
 * {@link Record#publishingPlaces}
 */
@Component
@Order(69)
public class DataFieldMapper984 implements DataFieldMapper {

    private PublishingPlaceRepository publishingPlaceRepository;

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        String name = getNameFromDataField(field);
        PublishingPlace place = publishingPlaceRepository.findByName(name);
        boolean isFromBook = record instanceof Book;
        place = place == null ? createPublishingPlace(field, isFromBook) : updatePublishingPlace(field, place, isFromBook);
        record.getPublishingPlaces().add(place);
    }

    private PublishingPlace createPublishingPlace(DataFieldType field, boolean isFromBook) {
        PublishingPlace place = new PublishingPlace();
        place.setFromBook(isFromBook);
        place.setFromIllustration(!isFromBook);
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    place.setName(subfield.getValue());
                    break;
                case CODE_B:
                    place.setCountry(subfield.getValue());
                    break;
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return place;
    }

    private PublishingPlace updatePublishingPlace(DataFieldType field, PublishingPlace place, boolean isFromBook) {
        if (!place.isFromBook()) place.setFromBook(isFromBook);
        if (!place.isFromIllustration()) place.setFromIllustration(!isFromBook);
        field.getSubfield().forEach(subfield -> {
            if (subfield.getCode().equals(CODE_B)) {
                place.setCountry(subfield.getValue());
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return place;
    }

    @Override
    public String getTag() {
        return TAG_984;
    }

    @Autowired
    public void setPublishingPlaceRepository(PublishingPlaceRepository publishingPlaceRepository) {
        this.publishingPlaceRepository = publishingPlaceRepository;
    }
}
