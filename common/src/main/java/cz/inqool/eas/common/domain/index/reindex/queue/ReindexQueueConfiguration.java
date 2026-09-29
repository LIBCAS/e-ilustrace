package cz.inqool.eas.common.domain.index.reindex.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for reindex queue subsystem.
 *
 * If application wants to use reindex queue subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 * @param <RQ_ENTITY> reindex queue type
 */
@Slf4j
public abstract class ReindexQueueConfiguration<RQ_ENTITY extends ReindexQueue<RQ_ENTITY>> {

    /**
     * Constructs {@link ReindexQueueStore} bean.
     */
    @Bean
    public ReindexQueueStore<RQ_ENTITY> reindexQueueStore() {
        ReindexQueueStore.ReindexQueueStoreBuilder<RQ_ENTITY> builder = ReindexQueueStore.builder();
        configure(builder);

        return builder.build();
    }

    /**
     * Method for configuration the {@link ReindexQueueStore}.
     *
     * @param builder Builder used for configuration
     */
    protected abstract void configure(ReindexQueueStore.ReindexQueueStoreBuilder<RQ_ENTITY> builder);
}
