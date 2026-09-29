package cz.inqool.eas.eil.xml;

import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.eil.download.DownloadManager;
import cz.inqool.eas.eil.download.ImageForDownload;
import cz.inqool.eas.eil.record.link.Link;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.illustration.Illustration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.AssertionUtils.isNull;
import static cz.inqool.eas.eil.config.exception.EilExceptionCode.ENTITY_ALREADY_EXISTS;

@Service
@Slf4j
public class MarcCreateProcessor extends MarcProcessor {

    private DownloadManager downloadManager;

    @Override
    protected Record getRecord(String identifier) {
        isNull(recordRepository.findIdByIdentifier(identifier), () -> new ForbiddenOperation(
                ENTITY_ALREADY_EXISTS,
                "Record with identifier " + identifier + " already exists in database. Import aborted."));

        String[] parts = identifier.split("_");
        Record record;
        if (parts.length == 1) {
            record = new Book();
        } else {
            record = new Illustration();
        }
        record.setIdentifier(identifier);
        return record;
    }

    @Override
    protected Record persist(Record record) {
        Record created = recordRepository.create(record);
        // reindex collections that are persisted via cascade on Record -> those are (apparently) not written to ES index
        subjectPersonRepository.reindex(created.getSubjectPersons().stream().map(DomainObject::getId).collect(Collectors.toList()));
        subjectInstitutionRepository.reindex(created.getSubjectInstitutions().stream().map(DomainObject::getId).collect(Collectors.toList()));
        subjectEntryRepository.reindex(created.getSubjectEntries().stream().map(DomainObject::getId).collect(Collectors.toList()));
        subjectPlaceRepository.reindex(created.getSubjectPlaces().stream().map(DomainObject::getId).collect(Collectors.toList()));
        genreRepository.reindex(created.getGenres().stream().map(DomainObject::getId).collect(Collectors.toList()));
        publishingPlaceRepository.reindex(created.getPublishingPlaces().stream().map(DomainObject::getId).collect(Collectors.toList()));
        recordAuthorRepository.reindex(record.getAuthors().stream().map(DomainObject::getId).collect(Collectors.toList()));
        keywordRepository.reindex(record.getKeywords().stream().map(DomainObject::getId).collect(Collectors.toList()));
        return created;
    }

    @Override
    protected void handleImageDownload(Record record) {
        Set<Link> links = record.getLinks().stream()
                .filter(MarcCreateProcessor::isForImageDownload)
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
    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }
}
