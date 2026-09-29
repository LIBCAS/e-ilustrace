package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.subject.person.SubjectPerson;
import cz.inqool.eas.eil.subject.person.SubjectPersonRepository;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;
import static cz.inqool.eas.eil.xml.mapper.Utils.*;

/**
 * {@link Record#subjectPersons}
 */
@Component
@Order(39)
public class DataFieldMapper600 implements DataFieldMapper {

    private SubjectPersonRepository personRepository;

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        boolean isFromBook = record instanceof Book;
        SubjectPerson data = parseSubjectPersonAttributes(field, isFromBook);
        SubjectPerson person = findSubjectPerson(data.getFullName(), data.getBirthYear(), data.getBirthYear());
        person = person == null ? data : updateSubjectPersonAttributes(field, person, isFromBook);
        record.getSubjectPersons().add(person);
    }

    private static SubjectPerson parseSubjectPersonAttributes(DataFieldType field, boolean isFromBook) {
        SubjectPerson person = new SubjectPerson();
        String[] nameParts = new String[3]; // stores parsed name elements in order 1) name 2) regnal number 3) name supplement
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    nameParts[0] = subfield.getValue();
                    break;
                case CODE_B:
                    nameParts[1] = subfield.getValue();
                    break;
                case CODE_C:
                    nameParts[2] = subfield.getValue();
                    break;
                case CODE_D:
                    setBirthDeathYears(person, subfield);
                    break;
                case CODE_7:
                    person.setAuthorityCode(subfield.getValue());
                    break;
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        String fullName = Arrays.stream(nameParts).filter(Objects::nonNull).collect(Collectors.joining(" "));
        person.setFullName(fullName);
        person.setFromBook(isFromBook);
        person.setFromIllustration(!isFromBook);
        return person;
    }

    private SubjectPerson findSubjectPerson(String fullName, String birthYear, String deathYear) {
        List<SubjectPerson> persons = personRepository.findByFullName(fullName);
        if (persons.isEmpty()) {
            return null;
        }
        if (persons.size() == 1) {
            return persons.get(0);
        } else {
            // more subject persons with given name were found -> find the right one using birth and death year
            return personRepository.findByFullNameAndYears(fullName, birthYear, deathYear);
        }
    }

    private static SubjectPerson updateSubjectPersonAttributes(DataFieldType field, SubjectPerson person, boolean isFromBook) {
        if (!person.isFromBook()) person.setFromBook(isFromBook);
        if (!person.isFromIllustration()) person.setFromIllustration(!isFromBook);
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_D:
                    setBirthDeathYears(person, subfield);
                    break;
                case CODE_7:
                    person.setAuthorityCode(subfield.getValue());
                    break;
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return person;
    }

    @Override
    public String getTag() {
        return TAG_600;
    }

    @Autowired
    public void setSubjectPersonRepository(SubjectPersonRepository personRepository) {
        this.personRepository = personRepository;
    }
}
