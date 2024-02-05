package cz.inqool.eas.eil.config.reindex.queue;

import cz.inqool.eas.common.domain.index.reindex.queue.ReindexQueueConfiguration;
import cz.inqool.eas.common.domain.index.reindex.queue.ReindexQueueStore;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EilReindexQueueConfiguration extends ReindexQueueConfiguration<EilReindexQueue> {

    @Override
    protected void configure(ReindexQueueStore.ReindexQueueStoreBuilder<EilReindexQueue> builder) {
        builder.type(EilReindexQueue.class);
    }
}
