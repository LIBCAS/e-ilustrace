package cz.inqool.eas.eil.xml;

import cz.inqool.eas.common.exception.v2.InvalidAttribute;
import cz.inqool.eas.eil.author.record.RecordAuthorRepository;
import cz.inqool.eas.eil.genre.GenreRepository;
import cz.inqool.eas.eil.keyword.KeywordRepository;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceRepository;
import cz.inqool.eas.eil.record.*;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.eas.eil.subject.entry.SubjectEntryRepository;
import cz.inqool.eas.eil.subject.institution.SubjectInstitutionRepository;
import cz.inqool.eas.eil.subject.person.SubjectPersonRepository;
import cz.inqool.eas.eil.subject.place.SubjectPlaceRepository;
import cz.inqool.eas.eil.xml.iimport.ImportService;
import cz.inqool.eas.eil.xml.mapper.datafield.DataFieldMapper;
import cz.inqool.eas.eil.xml.mapper.datafield.df264.DataFieldMapper264;
import gov.loc.marc21.slim.ControlFieldType;
import gov.loc.marc21.slim.DataFieldType;
import gov.loc.marc21.slim.RecordType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ATTRIBUTE_VALUE_IS_NULL;
import static cz.inqool.eas.eil.xml.mapper.Constants.*;

/**
 * Service that handles processing of MARC21 (XML) record.
 * Makes use of Strategy pattern for parsing data from specific datafields and their subfields using DataFieldMappers
 *
 * Implementations:
 * - MarcCreateProcessor: creates new Record from given XML file
 * - MarcUpdateProcessor: updates existing Record. XML that is used to update particular Record contains all the data
 *                        that the initial file contained + changes in updated attributes (datafields and subfields)
 */
@Slf4j
public abstract class MarcProcessor {

    protected List<DataFieldMapper> fieldMappers;

    protected RecordRepository recordRepository;

    protected ImportService importService;

    protected SubjectPersonRepository subjectPersonRepository;

    protected SubjectInstitutionRepository subjectInstitutionRepository;

    protected SubjectEntryRepository subjectEntryRepository;

    protected SubjectPlaceRepository subjectPlaceRepository;

    protected GenreRepository genreRepository;

    protected PublishingPlaceRepository publishingPlaceRepository;

    protected RecordAuthorRepository recordAuthorRepository;
    protected KeywordRepository keywordRepository;

    @Transactional
    public String parseRecord(RecordType xmlRecord) {
        Map<String, ControlFieldType> controlFieldsMap = xmlRecord.getControlfield().stream()
                .collect(Collectors.toMap(ControlFieldType::getTag, Function.identity()));
        Map<String, List<DataFieldType>> dataFieldsMap = xmlRecord.getDatafield().stream()
                .collect(Collectors.groupingBy(DataFieldType::getTag));

        String identifier = getControlFieldValue(TAG_001, controlFieldsMap);
        if (identifier.contains(" ")) {
            log.warn("Record identifier '{}' contains whitespaces. Parsing of record aborted", identifier);
            return null;
        }
        Record record = getRecord(identifier);
        if (record instanceof Illustration && ((Illustration) record).getBook() == null) {
            setBookToIllustration((Illustration) record);
        }
        parseTimeRangeFromControlField(record, getControlFieldValue(TAG_008, controlFieldsMap));
        List<DataFieldMapper> fieldMappers = selectAppropriateFieldMappers(record);

        for (DataFieldMapper fieldMapper : fieldMappers) {
            List<DataFieldType> fields = dataFieldsMap.get(fieldMapper.getTag());
            // field 264 and 264 with ind2="3" need to be handled separately by two distinct mappers - different semantics
            if (fieldMapper.getTag().equals(TAG_264)) {
                String indicator = ((DataFieldMapper264) fieldMapper).getIndicator();
                fields = selectFieldsByIndicator(indicator, fields);
            }
            if (fields != null) {
                for (DataFieldType field : fields) {
                    fieldMapper.toAttribute(field, record);
                }
            }
        }
        record = persist(record);
        handleImageDownload(record);
        log.info("Processing of XML record " + record.getIdentifier() + " finished successfully.");
        return record.id;
    }

    protected abstract Record getRecord(String identifier);

    protected abstract Record persist(Record record);

    protected abstract void handleImageDownload(Record record);

    private String getControlFieldValue(String tag, Map<String, ControlFieldType> controlFieldsMap) {
        ControlFieldType field = controlFieldsMap.get(tag);
        if (field == null || field.getValue().isBlank()) {
            throw new InvalidAttribute(ATTRIBUTE_VALUE_IS_NULL, "Control field " + tag + " in imported XML file " +
                    "is missing although required. Import aborted.");
        }
        return field.getValue();
    }

    private void parseTimeRangeFromControlField(Record record, String fixedLengthField) {
        String stringYearFrom = fixedLengthField.substring(7, 11);
        String stringYearTo = fixedLengthField.substring(11, 15);

        try {
            int yearFrom = stringYearFrom.trim().length() > 0 ? Integer.parseInt(stringYearFrom) : 0;
            record.setYearFrom(yearFrom);
            int yearTo = stringYearTo.trim().length() > 0 ? Integer.parseInt(stringYearTo) : 0;
            record.setYearTo(yearTo);
        } catch (NumberFormatException ex) {
            log.warn("Error parsing publication years from fixed length field '" + fixedLengthField + "'");
        }
        record.setFixedLengthField(fixedLengthField);
    }

    private void setBookToIllustration(Illustration illustration) {
        String[] splitIdentifier = illustration.getIdentifier().split("_");
        String bookIdentifier = splitIdentifier[0];
        String bookId = recordRepository.findIdByIdentifier(bookIdentifier);
        if (bookId == null) {
            Path xml = importService.findBookSourceFile(bookIdentifier);
            if (xml == null) {
                log.info("Book XML record " + bookIdentifier + " not found in current output subdirectory, book record not created");
            } else {
                File file = xml.toFile();
                bookId = importService.unmarshallAndParseRecord(file);
            }
        }
        // TODO ask customers whether book records (if not found in DB) should be automatically downloaded from external API
        if (bookId != null) {
            Book saved = (Book) recordRepository.findById(bookId);
            illustration.setBook(saved);
        }
    }

    private List<DataFieldType> selectFieldsByIndicator(String indicator, List<DataFieldType> fields) {
        if (indicator.equals(IND_3)) {
            return fields.stream().filter(f -> f.getInd2().equals(IND_3)).collect(Collectors.toList());
        }
        return fields.stream().filter(f -> !f.getInd2().equals(IND_3)).collect(Collectors.toList());
    }

    private List<DataFieldMapper> selectAppropriateFieldMappers(Record record) {
        if (record instanceof Illustration) {
            return fieldMappers.stream().filter(DataFieldMapper::isIllustrationMapper).collect(Collectors.toList());
        }
        return fieldMappers.stream().filter(DataFieldMapper::isBookMapper).collect(Collectors.toList());
    }

    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Autowired
    public void setImportService(ImportService importService) {
        this.importService = importService;
    }

    @Autowired
    public void setFieldMappers(List<DataFieldMapper> fieldMappers) {
        this.fieldMappers = fieldMappers;
    }

    @Autowired
    public void setSubjectInstitutionRepository(SubjectInstitutionRepository subjectInstitutionRepository) {
        this.subjectInstitutionRepository = subjectInstitutionRepository;
    }

    @Autowired
    public void setSubjectPersonRepository(SubjectPersonRepository subjectPersonRepository) {
        this.subjectPersonRepository = subjectPersonRepository;
    }

    @Autowired
    public void setSubjectEntryRepository(SubjectEntryRepository subjectEntryRepository) {
        this.subjectEntryRepository = subjectEntryRepository;
    }

    @Autowired
    public void setSubjectPlaceRepository(SubjectPlaceRepository subjectPlaceRepository) {
        this.subjectPlaceRepository = subjectPlaceRepository;
    }

    @Autowired
    public void setGenreRepository(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Autowired
    public void setPublishingPlaceRepository(PublishingPlaceRepository publishingPlaceRepository) {
        this.publishingPlaceRepository = publishingPlaceRepository;
    }

    @Autowired
    public void setRecordAuthorRepository(RecordAuthorRepository recordAuthorRepository) {
        this.recordAuthorRepository = recordAuthorRepository;
    }

    @Autowired
    public void setKeywordRepository(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }
}
