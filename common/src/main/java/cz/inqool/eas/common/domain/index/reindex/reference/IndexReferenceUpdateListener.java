package cz.inqool.eas.common.domain.index.reindex.reference;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.index.reindex.reference.entity.IndexReferenceUpdate;
import cz.inqool.eas.common.domain.index.reindex.reference.entity.IndexReferenceUpdateStore;
import cz.inqool.eas.common.domain.index.reindex.reference.event.ReferenceUpdated;
import cz.inqool.eas.common.domain.index.reindex.reference.event.ReferencedCollectionUpdated;
import cz.inqool.eas.common.domain.index.reindex.reference.event.ReferencedObjectUpdated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Listener for changes on entities referenced in index object classes.
 */
@Slf4j
public class IndexReferenceUpdateListener implements ApplicationListener<ReferenceUpdated<?>> {

    private IndexReferenceHolder indexReferenceHolder;

    private IndexReferenceUpdateStore store;

    private PlatformTransactionManager transactionManager;


    @Override
    public void onApplicationEvent(@NonNull ReferenceUpdated<?> event) {
        log.trace("Received event: " + event);

        TransactionTemplate transaction = new TransactionTemplate(this.transactionManager);

        if (event instanceof ReferencedObjectUpdated) {
            ReferencedObjectUpdated<?> objectEvent = (ReferencedObjectUpdated<?>) event;
            transaction.executeWithoutResult(s ->
                    createReferenceUpdates(Set.of(objectEvent.getValue()), event.getUpdatedFields())
            );
        } else if (event instanceof ReferencedCollectionUpdated) {
            ReferencedCollectionUpdated<?> collectionEvent = (ReferencedCollectionUpdated<?>) event;
            transaction.executeWithoutResult(s ->
                    createReferenceUpdates(collectionEvent.getValue(), event.getUpdatedFields())
            );
        } else {
            throw new IllegalArgumentException("Event type '" + event.getClass() + "' not supported.");
        }
    }

    private void createReferenceUpdates(Collection<? extends Domain<?>> updatedValues, Set<String> updatedFields) {
        Set<IndexReferenceUpdate> referenceUpdates = new LinkedHashSet<>();

        for (Domain<?> updatedValue : updatedValues) {
            Class<? extends Domain<?>> type = (Class<? extends Domain<?>>) updatedValue.getClass();

            indexReferenceHolder.getReferences(type).stream()
                    .filter(indexReferenceField -> {
                        Set<String> interestedFields = indexReferenceField.getOnlyOnChanged();
                        if (interestedFields == null) { // all fields are interrested
                            return updatedFields == null || !updatedFields.isEmpty(); // at least one field was updated
                        } else { // there are specified fields of interest
                            if (updatedFields == null) { // all fields were updated
                                return !interestedFields.isEmpty(); // at least one field is of interest
                            } else { // only some fields were updated
                                return CollectionUtils.containsAny(updatedFields, interestedFields); // some interrested fields were updated
                            }
                        }
                    })
                    .map(indexReferenceField -> IndexReferenceUpdate.of(indexReferenceField, updatedValue))
                    .forEach(referenceUpdates::add);
        }

        log.trace("{} active index references found.", referenceUpdates.size());
        if (!referenceUpdates.isEmpty()) {
            store.create(referenceUpdates);
        }
    }


    @Autowired
    public void setIndexReferenceHolder(IndexReferenceHolder holder) {
        this.indexReferenceHolder = holder;
    }

    @Autowired
    public void setStore(IndexReferenceUpdateStore store) {
        this.store = store;
    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
