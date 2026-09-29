package cz.inqool.eas.eil.author.record;

import cz.inqool.eas.common.domain.DomainRepository;
import cz.inqool.eas.common.domain.index.DomainIndex;
import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class RecordAuthorRepository extends DomainRepository<
        RecordAuthor,
        RecordAuthorIndexed,
        RecordAuthorIndexedObject,
        DomainStore<RecordAuthor, RecordAuthor, QRecordAuthor>,
        DomainIndex<RecordAuthor, RecordAuthorIndexed, RecordAuthorIndexedObject>> {

    @Override
    public int getReindexBatchSize() {
        return 1000;
    }
}
