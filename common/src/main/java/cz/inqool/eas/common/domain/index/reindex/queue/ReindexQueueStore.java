package cz.inqool.eas.common.domain.index.reindex.queue;

import com.querydsl.core.types.dsl.EntityPathBase;
import cz.inqool.eas.common.domain.store.DomainStore;
import lombok.Builder;

public class ReindexQueueStore<RQ_ENTITY extends ReindexQueue<RQ_ENTITY>> extends DomainStore<RQ_ENTITY, RQ_ENTITY, EntityPathBase<RQ_ENTITY>> {

    @Builder
    public ReindexQueueStore(Class<RQ_ENTITY> type) {
        super(type);
    }
}
