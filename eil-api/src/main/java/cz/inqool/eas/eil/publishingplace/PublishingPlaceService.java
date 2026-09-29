package cz.inqool.eas.eil.publishingplace;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.RecordRepository;
import cz.inqool.eas.eil.record.book.BookIndexed;
import cz.inqool.eas.eil.record.illustration.IllustrationIndexed;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.constraints.NotNull;

@Service
public class PublishingPlaceService extends DatedService<
        PublishingPlace,
        PublishingPlaceDetail,
        PublishingPlaceList,
        PublishingPlaceCreate,
        PublishingPlaceUpdate,
        PublishingPlaceRepository
        > {

    private RecordRepository recordRepository;
    private TransactionTemplate transactionTemplate;

    public void checkSources(Record record) {
        String recordId = record.getId();
        for (PublishingPlace place : record.getPublishingPlaces()) {
            PublishingPlaceIndexed placeIndexed = PublishingPlaceIndexed.toView(place);
            boolean update = false;
            if (place.isFromBook()) {
                BookIndexed bookIndexed = recordRepository.findAnyPublishingPlaceInBook(recordId, placeIndexed);
                if (bookIndexed == null) {
                    place.setFromBook(false);
                    update = true;
                }
            }

            if (place.isFromIllustration()) {
                IllustrationIndexed illustrationIndexed = recordRepository.findAnyPublishingPlaceInIll(recordId, placeIndexed);
                if (illustrationIndexed == null) {
                    place.setFromIllustration(false);
                    update = true;
                }
            }

            if (update) transactionTemplate.executeWithoutResult(status -> repository.update(place));
        }
    }

    @Override
    protected void preCreateHook(PublishingPlace object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(PublishingPlace object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
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
