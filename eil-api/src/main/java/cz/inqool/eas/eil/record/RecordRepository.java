package cz.inqool.eas.eil.record;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceIndexed;
import cz.inqool.eas.eil.record.book.BookIndexed;
import cz.inqool.eas.eil.record.book.QBook;
import cz.inqool.eas.eil.record.book.QBookIndexed;
import cz.inqool.eas.eil.record.illustration.*;
import cz.inqool.eas.eil.subject.entry.SubjectEntryIndexed;
import cz.inqool.eas.eil.subject.person.SubjectPersonIndexed;
import cz.inqool.eas.eil.subject.place.SubjectPlaceIndexed;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecordRepository extends DatedRepository<
        Record,
        RecordIndexed,
        RecordIndexedObject,
        DatedStore<Record, Record, QRecord>,
        DatedIndex<Record, RecordIndexed, RecordIndexedObject>> {

    public String findIdByIdentifier(String identifier) {
        RecordIdentifier obj = findByIdentifier(identifier);
        if (obj == null) {
            return null;
        }
        return obj.id;
    }

    public RecordIdentifier findByIdentifier(String identifier) {
        QRecordIdentifier model = QRecordIdentifier.recordIdentifier;

        RecordIdentifier record = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.identifier.eq(identifier))
                .fetchOne();

        detachAll();
        return record;
    }

    public IllustrationXlsx findXlsxByIdentifier(String identifier) {
        QIllustrationXlsx model = QIllustrationXlsx.illustrationXlsx;

        IllustrationXlsx ill = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.identifier.eq(identifier))
                .fetchOne();

        detachAll();
        return ill;
    }

    public Record findById(String id) {
        QRecord model = QRecord.record;

        Record record = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.id.eq(id))
                .fetchOne();

        detachAll();
        return record;
    }

    public List<RecordEssential> findAllEssential() {
        QIllustrationEssential model = QIllustrationEssential.illustrationEssential;

        List<RecordEssential> ills = query()
                .select(model._super)
                .from(model)
                .where(model.deleted.isNull())
                .fetch();

        detachAll();
        return ills;
    }

    public List<RecordEssential> findIllustrationScansVise() {
        QIllustrationEssential model = QIllustrationEssential.illustrationEssential;

        List<RecordEssential> ills = query()
                .select(model._super)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.illustrationScan.isNotNull())
                .where(model.viseFileId.isNull())
                .where(model.viseIllScanCopied.isNull())
                .fetch();

        detachAll();
        return ills;
    }

    public List<RecordEssential> findNullViseFileIdWithIllustrationScan() {
        QIllustrationEssential model = QIllustrationEssential.illustrationEssential;

        List<RecordEssential> ills = query()
                .select(model._super)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.illustrationScan.isNotNull())
                .where(model.viseFileId.isNull())
                .where(model.viseIllScanCopied.isNotNull())
                .fetch();

        detachAll();
        return ills;
    }

    public List<IllustrationVise> findIllustrationsWithViseId() {
        QIllustrationVise model = QIllustrationVise.illustrationVise;

        List<IllustrationVise> ills = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.viseFileId.isNotNull())
                .fetch();

        detachAll();
        return ills;
    }

    public List<RecordEssential> findIllustrationScans() {
        QIllustrationEssential model = QIllustrationEssential.illustrationEssential;

        List<RecordEssential> ills = query()
                .select(model._super)
                .from(model)
                .where(model.deleted.isNull())
                .where((model.illustrationScan.isNotNull().and(model.cantaloupeIllScanCopied.isNull()))
                        .or(model.pageScan.isNotNull().and(model.cantaloupePageScanCopied.isNull())))
                .fetch();

        detachAll();
        return ills;
    }

    public List<RecordEssential> findNullCantaloupeFileIds() {
        QIllustrationEssential model = QIllustrationEssential.illustrationEssential;

        List<RecordEssential> ills = query()
                .select(model._super)
                .from(model)
                .where(model.deleted.isNull())
                .where((model.pageScan.isNotNull().and(model.cantaloupePageScanId.isNull()).and(model.cantaloupePageScanCopied.isNotNull())).
                        or(model.illustrationScan.isNotNull().and(model.cantaloupeIllScanId.isNull().and(model.cantaloupeIllScanCopied.isNotNull()))))
                .fetch();

        detachAll();
        return ills;
    }

    public Integer getMinYearFromIllustration() {
        QIllustration model = QIllustration.illustration;

        Integer yearFrom = query()
                .select(model.yearFrom.min())
                .from(model)
                .where(model.deleted.isNull())
                .fetchFirst();

        detachAll();
        return yearFrom;
    }

    public Integer getMaxYearToIllustration() {
        QIllustration model = QIllustration.illustration;

        Integer yearTo = query()
                .select(model.yearFrom.max())
                .from(model)
                .where(model.deleted.isNull())
                .fetchFirst();

        detachAll();
        return yearTo;
    }

    public Integer getMinYearFromBook() {
        QBook model = QBook.book;

        Integer yearFrom = query()
                .select(model.yearFrom.min())
                .from(model)
                .where(model.deleted.isNull())
                .fetchFirst();

        detachAll();
        return yearFrom;
    }

    public Integer getMaxYearToBook() {
        QBook model = QBook.book;

        Integer yearTo = query()
                .select(model.yearFrom.max())
                .from(model)
                .where(model.deleted.isNull())
                .fetchFirst();

        detachAll();
        return yearTo;
    }

    public List<RecordSources> listNonDeleted() {
        QRecordSources model = QRecordSources.recordSources;

        List<RecordSources> records = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .fetch();

        detachAll();
        return records;
    }

    public BookIndexed findAnyPublishingPlaceInBook(String id, PublishingPlaceIndexed placeIndexed) {
        QBookIndexed model = QBookIndexed.bookIndexed;

        BookIndexed ill = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.id.ne(id))
                .where(model.publishingPlaces.contains(placeIndexed))
                .fetchFirst();

        detachAll();
        return ill;
    }

    public IllustrationIndexed findAnyPublishingPlaceInIll(String id, PublishingPlaceIndexed placeIndexed) {
        QIllustrationIndexed model = QIllustrationIndexed.illustrationIndexed;

        IllustrationIndexed book = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.id.ne(id))
                .where(model.publishingPlaces.contains(placeIndexed))
                .fetchFirst();

        detachAll();
        return book;
    }

    public BookIndexed findAnyEntryInBook(String id, SubjectEntryIndexed entryIndexed) {
        QBookIndexed model = QBookIndexed.bookIndexed;

        BookIndexed ill = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.id.ne(id))
                .where(model.subjectEntries.contains(entryIndexed))
                .fetchFirst();

        detachAll();
        return ill;
    }

    public IllustrationIndexed findAnyEntryInIll(String id, SubjectEntryIndexed entryIndexed) {
        QIllustrationIndexed model = QIllustrationIndexed.illustrationIndexed;

        IllustrationIndexed book = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.id.ne(id))
                .where(model.subjectEntries.contains(entryIndexed))
                .fetchFirst();

        detachAll();
        return book;
    }

    public BookIndexed findAnyPersonInBook(String id, SubjectPersonIndexed personIndexed) {
        QBookIndexed model = QBookIndexed.bookIndexed;

        BookIndexed ill = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.id.ne(id))
                .where(model.subjectPersons.contains(personIndexed))
                .fetchFirst();

        detachAll();
        return ill;
    }

    public IllustrationIndexed findAnyPersonInIll(String id, SubjectPersonIndexed personIndexed) {
        QIllustrationIndexed model = QIllustrationIndexed.illustrationIndexed;

        IllustrationIndexed book = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.id.ne(id))
                .where(model.subjectPersons.contains(personIndexed))
                .fetchFirst();

        detachAll();
        return book;
    }

    public BookIndexed findAnyPlaceInBook(String id, SubjectPlaceIndexed placeIndexed) {
        QBookIndexed model = QBookIndexed.bookIndexed;

        BookIndexed ill = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.id.ne(id))
                .where(model.subjectPlaces.contains(placeIndexed))
                .fetchFirst();

        detachAll();
        return ill;
    }

    public IllustrationIndexed findAnyPlaceInIll(String id, SubjectPlaceIndexed placeIndexed) {
        QIllustrationIndexed model = QIllustrationIndexed.illustrationIndexed;

        IllustrationIndexed book = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.id.ne(id))
                .where(model.subjectPlaces.contains(placeIndexed))
                .fetchFirst();

        detachAll();
        return book;
    }

    public Record findByIdentifierEntity(String identifier) {
        QRecord model = QRecord.record;

        Record record = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.identifier.eq(identifier))
                .fetchOne();

        detachAll();
        return record;
    }

    public IllustrationIdentifier findIllByBookIdentifier(String identifier) {
        QIllustrationIdentifier model = QIllustrationIdentifier.illustrationIdentifier;

        IllustrationIdentifier illustration = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.identifier.startsWith(identifier))
                .fetchFirst();

        detachAll();
        return illustration;
    }

    @Override
    public int getReindexBatchSize() {
        return 1000;
    }
}
