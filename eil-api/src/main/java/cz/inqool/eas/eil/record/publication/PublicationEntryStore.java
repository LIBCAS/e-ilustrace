package cz.inqool.eas.eil.record.publication;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class PublicationEntryStore extends DomainStore<PublicationEntry, PublicationEntry, QPublicationEntry> {

    public PublicationEntryStore() {
        super(PublicationEntry.class);
    }
}
