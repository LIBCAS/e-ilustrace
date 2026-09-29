package cz.inqool.eas.eil.xml;

import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.eil.download.DownloadManager;
import cz.inqool.eas.eil.download.ImageForDownload;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceService;
import cz.inqool.eas.eil.record.Record;

import cz.inqool.eas.eil.record.RecordMarc;
import cz.inqool.eas.eil.record.RecordService;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.eas.eil.record.illustration.IllustrationMarc;
import cz.inqool.eas.eil.record.link.Link;
import cz.inqool.eas.eil.subject.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

@Service
@Slf4j
public class MarcUpdateProcessor extends MarcProcessor {

    private RecordService recordService;
    private DownloadManager downloadManager;
    private SubjectService subjectService;
    private PublishingPlaceService publishingPlaceService;

    @Override
    protected Record getRecord(String identifier) {
        Record recordEntity = recordRepository.findByIdentifierEntity(identifier);
        notNull(recordEntity, () -> new ForbiddenOperation(
                ENTITY_NOT_EXIST,
                "Record with identifier " + identifier + " does not exist in database but was intended to be updated."));

        subjectService.checkSources(recordEntity);
        publishingPlaceService.checkSources(recordEntity);
        RecordMarc record = RecordMarc.toView(recordEntity);
        record = recordService.updateRecord(record);
        Record update = record instanceof IllustrationMarc ? new Illustration() : new Book();
        update.setId(record.id);
        update.setIdentifier(identifier);
        update.setIconclass(record.getIconclass());
        update.setThemes(record.getThemes());
        if (update instanceof Illustration) {
            ((Illustration) update).setIconclassState(((IllustrationMarc) record).getIconclassState());
            ((Illustration) update).setThemeState(((IllustrationMarc) record).getThemeState());
        }
        return update;
    }

    @Override
    protected Record persist(Record record) {
        Record updated = recordRepository.update(record);
        subjectPersonRepository.reindex(updated.getSubjectPersons().stream().map(DomainObject::getId).collect(Collectors.toList()));
        subjectInstitutionRepository.reindex(updated.getSubjectInstitutions().stream().map(DomainObject::getId).collect(Collectors.toList()));
        subjectEntryRepository.reindex(updated.getSubjectEntries().stream().map(DomainObject::getId).collect(Collectors.toList()));
        subjectPlaceRepository.reindex(updated.getSubjectPlaces().stream().map(DomainObject::getId).collect(Collectors.toList()));
        genreRepository.reindex(updated.getGenres().stream().map(DomainObject::getId).collect(Collectors.toList()));
        publishingPlaceRepository.reindex(updated.getPublishingPlaces().stream().map(DomainObject::getId).collect(Collectors.toList()));
        recordAuthorRepository.reindex(record.getAuthors().stream().map(DomainObject::getId).collect(Collectors.toList()));
        keywordRepository.reindex(record.getKeywords().stream().map(DomainObject::getId).collect(Collectors.toList()));
        return updated;
    }

    @Override
    protected void handleImageDownload(Record record) {
        Set<Link> links = record.getLinks().stream()
                .filter(MarcUpdateProcessor::isForImageDownload)
                .collect(Collectors.toSet());
        downloadManager.queueImageDownloads(links);
    }

    private static boolean isForImageDownload(Link link) {
        String description = link.getDescription().trim().toLowerCase();
        return Arrays.stream(ImageForDownload.values())
                .map(ImageForDownload::getLabel)
                .anyMatch(description::startsWith);
    }

    @Autowired
    public void setRecordService(RecordService recordService) {
        this.recordService = recordService;
    }

    @Autowired
    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Autowired
    public void setPublishingPlaceService(PublishingPlaceService publishingPlaceService) {
        this.publishingPlaceService = publishingPlaceService;
    }
}
