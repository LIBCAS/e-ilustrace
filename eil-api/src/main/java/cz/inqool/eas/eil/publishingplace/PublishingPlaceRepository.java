package cz.inqool.eas.eil.publishingplace;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class PublishingPlaceRepository extends DatedRepository<
        PublishingPlace,
        PublishingPlaceIndexed,
        PublishingPlaceIndexedObject,
        DatedStore<PublishingPlace, PublishingPlace, QPublishingPlace>,
        DatedIndex<PublishingPlace, PublishingPlaceIndexed, PublishingPlaceIndexedObject>> {

    public PublishingPlace findByName(String name) {
        if (name == null) {
            return null;
        }
        QPublishingPlace model = QPublishingPlace.publishingPlace;

        PublishingPlace publishingPlace = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.name.eq(name))
                .fetchOne();

        detachAll();
        return publishingPlace;
    }

    @Override
    public int getReindexBatchSize() {
        return 1000;
    }
}
