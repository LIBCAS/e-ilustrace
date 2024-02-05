package cz.inqool.eas.eil.record;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.exception.v2.InvalidArgument;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.utils.AssertionUtils;
import cz.inqool.eas.eil.author.record.RecordAuthor;
import cz.inqool.eas.eil.author.record.RecordAuthorRepository;
import cz.inqool.eas.eil.download.DownloadReference;
import cz.inqool.eas.eil.download.DownloadReferenceStore;
import cz.inqool.eas.eil.mirador.MiradorService;
import cz.inqool.eas.eil.mirador.dto.Manifest;
import cz.inqool.eas.eil.publishingplace.PublishingPlace;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceRepository;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceService;
import cz.inqool.eas.eil.record.book.BookMarc;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.book.BookSources;
import cz.inqool.eas.eil.record.dto.RecordYearsDto;
import cz.inqool.eas.eil.record.illustration.*;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import cz.inqool.eas.eil.subject.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.constraints.NotNull;
import java.util.*;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.eil.exception.EilExceptionCode.WRONG_ARGUMENT_VALUE;

@Service
@Slf4j
public class RecordService extends DatedService<
        Record,
        RecordDetail,
        RecordList,
        RecordCreate,
        RecordUpdate,
        RecordRepository
        > {

    private FileManager fileManager;;
    private MiradorService miradorService;
    private DownloadReferenceStore downloadReferenceStore;
    private SubjectService subjectService;
    private PublishingPlaceRepository publishingPlaceRepository;
    private RecordAuthorRepository recordAuthorRepository;
    private TransactionTemplate transactionTemplate;
    private PublishingPlaceService publishingPlaceService;

    @Transactional
    public IllustrationDetail setIconclassState(String id, IconclassThemeState state) {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        Record record = repository.find(id);
        AssertionUtils.notNull(record, () -> new MissingObject(ENTITY_NOT_EXIST));
        if (record instanceof Illustration) {
            Illustration ill = (Illustration) record;
            ill.setIconclassState(state);
            repository.update(ill);
        }
        return repository.find(IllustrationDetail.class, id);
    }

    @Transactional
    public IllustrationDetail setThemeState(String id, IconclassThemeState state) {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        Record record = repository.find(id);
        AssertionUtils.notNull(record, () -> new MissingObject(ENTITY_NOT_EXIST));
        if (record instanceof Illustration) {
            Illustration ill = (Illustration) record;
            ill.setThemeState(state);
            repository.update(ill);
        }
        return repository.find(IllustrationDetail.class, id);
    }

    public Manifest getManifest(String id) {
        Record record = repository.find(id);
        if (record instanceof Illustration) {
            return miradorService.createManifest((Illustration) record);
        } else if (record instanceof Book) {
            return miradorService.createManifest((Book) record);
        }
        return null;
    }

    public RecordYearsDto getYearsRange(String type) {
        AssertionUtils.isTrue(type.equalsIgnoreCase("illustration") || type.equalsIgnoreCase("book"),
                () -> new InvalidArgument(WRONG_ARGUMENT_VALUE));
        RecordYearsDto result = new RecordYearsDto();
        Integer yearFrom;
        Integer yearTo;
        if (type.equalsIgnoreCase("illustration")) {
            yearFrom = repository.getMinYearFromIllustration();
            yearTo = repository.getMaxYearToIllustration();
            result.setYearFrom(yearFrom == null ? 0 : yearFrom);
            result.setYearTo(yearTo == null ? 0 : yearTo);
        }
        if (type.equalsIgnoreCase("book")) {
            yearFrom = repository.getMinYearFromBook();
            yearTo = repository.getMaxYearToBook();
            result.setYearFrom(yearFrom == null ? 0 : yearFrom);
            result.setYearTo(yearTo == null ? 0 : yearTo);
        }
        return result;
    }

    @Transactional
    public RecordMarc updateRecord(RecordMarc record) {
        if (record instanceof IllustrationMarc) {
            File illScan = ((IllustrationMarc) record).getIllustrationScan();
            File illPageScan = ((IllustrationMarc) record).getPageScan();
            ((IllustrationMarc) record).setIllustrationScan(null);
            ((IllustrationMarc) record).setPageScan(null);
            if (illScan != null)
                fileManager.remove(illScan.getId());
            if (illPageScan != null)
                fileManager.remove(illPageScan.getId());

            ((IllustrationMarc) record).setViseFileId(null);
            ((IllustrationMarc) record).setViseIllScanCopied(null);

            ((IllustrationMarc) record).setCantaloupeIllScanId(null);
            ((IllustrationMarc) record).setCantaloupeIllScanCopied(null);

            ((IllustrationMarc) record).setCantaloupePageScanId(null);
            ((IllustrationMarc) record).setCantaloupePageScanCopied(null);
        } else {
            File frontPageScan = ((BookMarc) record).getFrontPageScan();
            ((BookMarc) record).setFrontPageScan(null);
            if (frontPageScan != null)
                fileManager.remove(frontPageScan.getId());
        }
        List<DownloadReference> references = downloadReferenceStore.fetchForRecord(record.getId());
        references.forEach(reference -> reference.setRecord(null));
        downloadReferenceStore.update(references);
        record.getKeywords().clear();
        repository.update(record);
        return record;
    }

    /**
     * Only used once, delete after used
     */
    public void updateSource() {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        List<RecordSources> records = repository.listNonDeleted();
        int count = 0;
        int total = records.size();
        log.debug("Start updating sources of {} records", total);
        for (RecordSources record : records) {
            boolean isFromBook = record instanceof BookSources;

            List<RecordAuthor> updateBatchRecordAuthor = new ArrayList<>();
            record.getAuthors().forEach(author -> {
                if (isFromBook) {
                    if (!author.isFromBook()) {
                        author.setFromBook(true);
                        updateBatchRecordAuthor.add(author);
                    }
                } else {
                    if (!author.isFromIllustration()) {
                        author.setFromIllustration(true);
                        updateBatchRecordAuthor.add(author);
                    }
                }
            });
            transactionTemplate.executeWithoutResult(status -> recordAuthorRepository.update(updateBatchRecordAuthor));

            List<PublishingPlace> updateBatchPublishingPlace = new ArrayList<>();
            record.getPublishingPlaces().forEach(place -> {
                if (isFromBook) {
                    if (!place.isFromBook()) {
                        place.setFromBook(true);
                        updateBatchPublishingPlace.add(place);
                    }
                } else {
                    if (!place.isFromIllustration()) {
                        place.setFromIllustration(true);
                        updateBatchPublishingPlace.add(place);
                    }
                }
            });
            transactionTemplate.executeWithoutResult(status -> publishingPlaceRepository.update(updateBatchPublishingPlace));

            subjectService.checkSourcesOneTime(record, isFromBook);
            count += 1;
            if (count % 100 == 0) {
                log.debug("Updated {}/{} records", count, total);
            }
        }
        log.debug("Finished updating sources");
    }

    @Override
    protected void preCreateHook(Record object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(Record object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void postDeleteHook(Record object) {
        super.postDeleteHook(object);
        publishingPlaceService.checkSources(object);
        subjectService.checkSources(object);
    }

    @Autowired
    public void setManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setMiradorService(MiradorService miradorService) {
        this.miradorService = miradorService;
    }

    @Autowired
    public void setDownloadReferenceStore(DownloadReferenceStore downloadReferenceStore) {
        this.downloadReferenceStore = downloadReferenceStore;
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
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
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Autowired
    public void setPublishingPlaceService(PublishingPlaceService publishingPlaceService) {
        this.publishingPlaceService = publishingPlaceService;
    }
}
