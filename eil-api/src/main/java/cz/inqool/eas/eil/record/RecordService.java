package cz.inqool.eas.eil.record;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.GteFilter;
import cz.inqool.eas.common.domain.index.dto.filter.LteFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.InvalidArgument;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.utils.AssertionUtils;
import cz.inqool.eas.eil.download.DownloadReference;
import cz.inqool.eas.eil.download.DownloadReferenceStore;
import cz.inqool.eas.eil.download.ImageForDownload;
import cz.inqool.eas.eil.mirador.MiradorService;
import cz.inqool.eas.eil.mirador.dto.Manifest;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceService;
import cz.inqool.eas.eil.record.book.BookMarc;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.dto.RecordFacetsDto;
import cz.inqool.eas.eil.record.dto.RecordYearsDto;
import cz.inqool.eas.eil.record.illustration.*;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import cz.inqool.eas.eil.subject.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;
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
    private PublishingPlaceService publishingPlaceService;
    private RecordCache recordCache;

    @Transactional
    public IllustrationDetail setIconclassState(String id, IconclassThemeState state) {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        Record record = repository.find(id);
        notNull(record, () -> new MissingObject(ENTITY_NOT_EXIST));
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
        notNull(record, () -> new MissingObject(ENTITY_NOT_EXIST));
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

    public RecordFacetsDto listFacets(@Nullable Params params, String type) {
        params = coalesce(params, Params::new);
        boolean isFromBook = type.equalsIgnoreCase("book");

        if (params.getFilters().stream().allMatch(f ->
                (f instanceof GteFilter) || (f instanceof LteFilter))) {
            RecordYearsDto yearsDto = getYearsRange(type);
            boolean min = params.getFilters().stream().filter(f -> f instanceof GteFilter)
                    .map(filter -> (GteFilter) filter)
                    .anyMatch(f -> Integer.parseInt(f.getValue()) == yearsDto.getYearFrom());
            boolean max = params.getFilters().stream().filter(f -> f instanceof LteFilter)
                    .map(filter -> (LteFilter) filter)
                    .anyMatch(f -> Integer.parseInt(f.getValue()) == yearsDto.getYearTo());
            //Default years range -> use cache and reload if necessary
            if (min && max) {
                if (isFromBook) {
                    params.addFilter(new EqFilter("type", "book"));
                    recordCache.reloadBooks(Instant.now(), params);
                    return recordCache.getBookCache();
                } else {
                    params.addFilter(new EqFilter("type", "illustration"));
                    recordCache.reloadIlls(Instant.now(), params);
                    return recordCache.getIllCache();
                }
            }
        }

        //Custom filters, therefore no cache and fetch needed
        if (isFromBook) {
            params.addFilter(new EqFilter("type", "book"));
            return recordCache.getBooks(params);
        } else {
            params.addFilter(new EqFilter("type", "illustration"));
            return recordCache.getIlls(params);
        }
    }

    @Transactional
    public void deleteImage(String id, ImageForDownload imageType) {
        UserChecker.userHasAnyPermission(Permission.ADMIN);
        Record record = repository.find(id);
        notNull(record, () -> new MissingObject(ENTITY_NOT_EXIST).details(details -> details.clazz(Record.class)));
        String fileId;
        File file;
        switch (imageType) {
            case ILLUSTRATION:
                file = ((Illustration) record).getIllustrationScan();
                notNull(file, () -> new MissingObject(ENTITY_NOT_EXIST).details(details -> details.clazz(File.class)));
                fileId = file.getId();
                ((Illustration) record).setIllustrationScan(null);
                break;
            case ILLUSTRATION_PAGE:
                file = ((Illustration) record).getPageScan();
                notNull(file, () -> new MissingObject(ENTITY_NOT_EXIST).details(details -> details.clazz(File.class)));
                fileId = file.getId();
                ((Illustration) record).setPageScan(null);
                break;
            case FRONT_PAGE:
                file = ((Book) record).getFrontPageScan();
                notNull(file, () -> new MissingObject(ENTITY_NOT_EXIST).details(details -> details.clazz(File.class)));
                fileId = file.getId();
                ((Book) record).setFrontPageScan(null);
                break;
            default:
                return;
        }
        repository.update(record);
        fileManager.remove(fileId);
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
    public void setPublishingPlaceService(PublishingPlaceService publishingPlaceService) {
        this.publishingPlaceService = publishingPlaceService;
    }

    @Autowired
    public void setRecordCache(RecordCache recordCache) {
        this.recordCache = recordCache;
    }
}
