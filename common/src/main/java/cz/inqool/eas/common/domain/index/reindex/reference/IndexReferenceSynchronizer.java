package cz.inqool.eas.common.domain.index.reindex.reference;

import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.DomainRepository;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.OrFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.domain.index.reindex.reference.entity.IndexReferenceUpdate;
import cz.inqool.eas.common.domain.index.reindex.reference.entity.IndexReferenceUpdateStore;
import cz.inqool.eas.common.utils.AopUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.JsonUtils.toJsonString;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

/**
 * Service for indexing references to other entities in elastic search, which are represented by {@link
 * IndexReferenceUpdate} entity and saved in local storage
 */
@Slf4j
public abstract class IndexReferenceSynchronizer {

    private Map<Class<? extends DomainIndexed<?, ?>>, DomainRepository<?, ?, ?, ?, ?>> indexRepositoryMap;

    private IndexReferenceUpdateStore store;
    private TransactionTemplate transactionTemplate;

    /**
     * Maximum ElasticSearch filter count allowed
     */
    private static final long FILTER_LIMIT = 200;


    /**
     * Implemented method needs to have annotation {@link Scheduled} present and configured, to run {@link
     * #reindexReferences()} method periodically.
     */
    public abstract void schedule();

    /**
     * Reindex all records saved in local storage (queue), which are represented by {@link IndexReferenceUpdate}
     * entity.
     * <p>
     * In order to improve the performance, collection of records is divided into separated batches for indexing.
     * <p>
     * Whole process must be executed in one transaction and it is required to log all important information during the
     * action
     */
    protected void reindexReferences() {
        if (indexRepositoryMap == null) {
            log.warn("There are no indexed stores present in this project. ");
            return;
        }

        log.trace("Reindexing references...");
        transactionTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        transactionTemplate.executeWithoutResult(status -> {
            Class<? extends DomainIndexed<?, ?>> firstType;
            while ((firstType = store.findFirstIndexedType()) != null) {
                if (!indexRepositoryMap.containsKey(firstType)) {
                    throw new IllegalStateException("There is no repository for type " + firstType);
                }

                DomainRepository<?, ?, ?, ?, ?> repository = indexRepositoryMap.get(firstType);

                List<IndexReferenceUpdate> indexReferenceUpdates = store.listFirstOf(firstType, FILTER_LIMIT);

                List<Filter> itemFilters = indexReferenceUpdates.stream()
                        .map(item -> new EqFilter(item.getElasticSearchPath(), item.getUpdatedEntityId()))
                        .collect(Collectors.toList());

                Params params = new Params();
                params.addFilter(new OrFilter(itemFilters));

                if (log.isTraceEnabled()) {
                    log.debug(toJsonString(params.getFilters()));
                }

                List<String> idToReindex = repository.listAllIdsByParams(params);

                if (!idToReindex.isEmpty()) {
                    repository.reindex(idToReindex);
                    log.debug("Reindexed {} entries of type '{}'", idToReindex.size(), firstType);
                }

                store.delete(indexReferenceUpdates);
            }
        });
        log.trace("References reindexed.");
    }


    @Autowired(required = false)
    public void setIndexRepositoryMap(List<DomainRepository<?, ?, ?, ?, ?>> domainRepositories) {
        this.indexRepositoryMap = domainRepositories.stream()
                .map(AopUtils::unwrap)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(DomainRepository::getIndexableType, Function.identity()));
    }

    @Autowired
    public void setStore(IndexReferenceUpdateStore store) {
        this.store = store;
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate template) {
        this.transactionTemplate = template;
    }
}
