package cz.inqool.eas.eil.subject;

import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.RecordRepository;
import cz.inqool.eas.eil.record.RecordSources;
import cz.inqool.eas.eil.record.book.BookIndexed;
import cz.inqool.eas.eil.record.illustration.IllustrationIndexed;
import cz.inqool.eas.eil.subject.entry.SubjectEntry;
import cz.inqool.eas.eil.subject.entry.SubjectEntryIndexed;
import cz.inqool.eas.eil.subject.entry.SubjectEntryRepository;
import cz.inqool.eas.eil.subject.person.SubjectPerson;
import cz.inqool.eas.eil.subject.person.SubjectPersonIndexed;
import cz.inqool.eas.eil.subject.person.SubjectPersonRepository;
import cz.inqool.eas.eil.subject.place.SubjectPlace;
import cz.inqool.eas.eil.subject.place.SubjectPlaceIndexed;
import cz.inqool.eas.eil.subject.place.SubjectPlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class SubjectService {
    private SubjectEntryRepository subjectEntryRepository;
    private SubjectPersonRepository subjectPersonRepository;
    private SubjectPlaceRepository subjectPlaceRepository;
    private RecordRepository recordRepository;
    private TransactionTemplate transactionTemplate;

    public void checkSources(Record record) {
        checkSourcesEntry(record);
        checkSourcesPerson(record);
        checkSourcesPlace(record);
    }

    /**
     * Only used once, delete after used
     */
    public void checkSourcesOneTime(RecordSources record, boolean isFromBook) {
        checkSourcesEntryOneTime(record, isFromBook);
        checkSourcesPersonOneTime(record, isFromBook);
        checkSourcesPlaceOneTime(record, isFromBook);
    }

    public void checkSourcesEntry(Record record) {
        String recordId = record.getId();
        for (SubjectEntry entry : record.getSubjectEntries()) {
            SubjectEntryIndexed entryIndexed = SubjectEntryIndexed.toView(entry);
            boolean update = false;
            if (entry.isFromBook()) {
                BookIndexed bookIndexed = recordRepository.findAnyEntryInBook(recordId, entryIndexed);
                if (bookIndexed == null) {
                    entry.setFromBook(false);
                    update = true;
                }
            }

            if (entry.isFromIllustration()) {
                IllustrationIndexed illustrationIndexed = recordRepository.findAnyEntryInIll(recordId, entryIndexed);
                if (illustrationIndexed == null) {
                    entry.setFromIllustration(false);
                    update = true;
                }
            }

            if (update) transactionTemplate.executeWithoutResult(status -> subjectEntryRepository.update(entry));
        }
    }

    public void checkSourcesPerson(Record record) {
        String recordId = record.getId();
        for (SubjectPerson person : record.getSubjectPersons()) {
            SubjectPersonIndexed personIndexed = SubjectPersonIndexed.toView(person);
            boolean update = false;
            if (person.isFromBook()) {
                BookIndexed bookIndexed = recordRepository.findAnyPersonInBook(recordId, personIndexed);
                if (bookIndexed == null) {
                    person.setFromBook(false);
                    update = true;
                }
            }

            if (person.isFromIllustration()) {
                IllustrationIndexed illustrationIndexed = recordRepository.findAnyPersonInIll(recordId, personIndexed);
                if (illustrationIndexed == null) {
                    person.setFromIllustration(false);
                    update = true;
                }
            }

            if (update) transactionTemplate.executeWithoutResult(status -> subjectPersonRepository.update(person));
        }
    }

    public void checkSourcesPlace(Record record) {
        String recordId = record.getId();
        for (SubjectPlace place : record.getSubjectPlaces()) {
            SubjectPlaceIndexed placeIndexed = SubjectPlaceIndexed.toView(place);
            boolean update = false;
            if (place.isFromBook()) {
                BookIndexed bookIndexed = recordRepository.findAnyPlaceInBook(recordId, placeIndexed);
                if (bookIndexed == null) {
                    place.setFromBook(false);
                    update = true;
                }
            }

            if (place.isFromIllustration()) {
                IllustrationIndexed illustrationIndexed = recordRepository.findAnyPlaceInIll(recordId, placeIndexed);
                if (illustrationIndexed == null) {
                    place.setFromIllustration(false);
                    update = true;
                }
            }

            if (update) transactionTemplate.executeWithoutResult(status -> subjectPlaceRepository.update(place));
        }
    }

    /**
     * Only used once, delete after used
     */
    public void checkSourcesEntryOneTime(RecordSources record, boolean isFromBook) {
        for (SubjectEntry entry : record.getSubjectEntries()) {
            boolean update = false;
            if (isFromBook) {
                if (!entry.isFromBook()) {
                    entry.setFromBook(true);
                    update = true;
                }
            } else {
                if (!entry.isFromIllustration()) {
                    entry.setFromIllustration(true);
                    update = true;
                }
            }
            if (update) transactionTemplate.executeWithoutResult(status -> subjectEntryRepository.update(entry));
        }
    }

    /**
     * Only used once, delete after used
     */
    public void checkSourcesPersonOneTime(RecordSources record, boolean isFromBook) {
        for (SubjectPerson person : record.getSubjectPersons()) {
            boolean update = false;
            if (isFromBook) {
                if (!person.isFromBook()) {
                    person.setFromBook(true);
                    update = true;
                }
            } else {
                if (!person.isFromIllustration()) {
                    person.setFromIllustration(true);
                    update = true;
                }
            }
            if (update) transactionTemplate.executeWithoutResult(status -> subjectPersonRepository.update(person));
        }
    }

    /**
     * Only used once, delete after used
     */
    public void checkSourcesPlaceOneTime(RecordSources record, boolean isFromBook) {
        for (SubjectPlace place : record.getSubjectPlaces()) {
            boolean update = false;
            if (isFromBook) {
                if (!place.isFromBook()) {
                    place.setFromBook(true);
                    update = true;
                }
            } else {
                if (!place.isFromIllustration()) {
                    place.setFromIllustration(true);
                    update = true;
                }
            }
            if (update) transactionTemplate.executeWithoutResult(status -> subjectPlaceRepository.update(place));
        }
    }
    @Autowired
    public void setSubjectEntryRepository(SubjectEntryRepository subjectEntryRepository) {
        this.subjectEntryRepository = subjectEntryRepository;
    }

    @Autowired
    public void setSubjectPersonRepository(SubjectPersonRepository subjectPersonRepository) {
        this.subjectPersonRepository = subjectPersonRepository;
    }

    @Autowired
    public void setSubjectPlaceRepository(SubjectPlaceRepository subjectPlaceRepository) {
        this.subjectPlaceRepository = subjectPlaceRepository;
    }

    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}