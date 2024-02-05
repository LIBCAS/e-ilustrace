package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.subject.entry.SubjectEntry;
import cz.inqool.eas.eil.subject.entry.SubjectEntryRepository;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;
import static cz.inqool.eas.eil.xml.mapper.Utils.getNameFromDataField;

/**
 * {@link Record#subjectEntries}
 */
@Component
@Order(45)
public class DataFieldMapper650 implements DataFieldMapper {

    private SubjectEntryRepository subjectEntryRepository;

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        String label = getNameFromDataField(field);
        SubjectEntry entry = subjectEntryRepository.findByLabel(label);
        boolean isFromBook = record instanceof Book;
        entry = entry == null ? createSubjectEntry(field, isFromBook) : updateSubjectEntry(field, entry, isFromBook);
        record.getSubjectEntries().add(entry);
    }

    protected SubjectEntry createSubjectEntry(DataFieldType field, boolean isFromBook) {
        SubjectEntry entry = new SubjectEntry();
        entry.setFromBook(isFromBook);
        entry.setFromIllustration(!isFromBook);
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    entry.setLabel(subfield.getValue());
                    break;
                case CODE_7:
                    entry.setAuthorityCode(subfield.getValue());
                    break;
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return entry;
    }

    private SubjectEntry updateSubjectEntry(DataFieldType field, SubjectEntry entry, boolean isFromBook) {
        if (!entry.isFromBook()) entry.setFromBook(isFromBook);
        if (!entry.isFromIllustration()) entry.setFromIllustration(!isFromBook);
        field.getSubfield().forEach(subfield -> {
            if (CODE_7.equals(subfield.getCode())) {
                entry.setAuthorityCode(subfield.getValue());
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return entry;
    }

    @Override
    public String getTag() {
        return TAG_650;
    }

    @Autowired
    public void setSubjectEntryRepository(SubjectEntryRepository subjectEntryRepository) {
        this.subjectEntryRepository = subjectEntryRepository;
    }
}
