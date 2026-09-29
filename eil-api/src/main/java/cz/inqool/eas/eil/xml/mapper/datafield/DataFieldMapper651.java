package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.subject.place.SubjectPlace;
import cz.inqool.eas.eil.subject.place.SubjectPlaceRepository;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;
import static cz.inqool.eas.eil.xml.mapper.Utils.getNameFromDataField;

/**
 * {@link Record#subjectPlaces}
 */
@Component
@Order(48)
public class DataFieldMapper651 implements DataFieldMapper {

    protected SubjectPlaceRepository subjectPlaceRepository;

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        String name = getNameFromDataField(field);
        SubjectPlace place = subjectPlaceRepository.findByName(name);
        boolean isFromBook = record instanceof Book;
        place = place == null ? createSubjectPlace(field, isFromBook) : updateSubjectPlace(field, place, isFromBook);
        record.getSubjectPlaces().add(place);
    }

    private SubjectPlace createSubjectPlace(DataFieldType field, boolean isFromBook) {
        SubjectPlace place = new SubjectPlace();
        place.setFromBook(isFromBook);
        place.setFromIllustration(!isFromBook);
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    place.setName(subfield.getValue());
                    break;
                case CODE_7:
                    place.setAuthorityCode(subfield.getValue());
                    break;
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return place;
    }

    private SubjectPlace updateSubjectPlace(DataFieldType field, SubjectPlace place, boolean isFromBook) {
        if (!place.isFromBook()) place.setFromBook(isFromBook);
        if (!place.isFromIllustration()) place.setFromIllustration(!isFromBook);
        field.getSubfield().forEach(subfield -> {
            if (CODE_7.equals(subfield.getCode())) {
                place.setAuthorityCode(subfield.getValue());
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return place;
    }

    @Override
    public String getTag() {
        return TAG_651;
    }

    @Autowired
    public void setSubjectPlaceRepository(SubjectPlaceRepository placeRepository) {
        this.subjectPlaceRepository = placeRepository;
    }
}
