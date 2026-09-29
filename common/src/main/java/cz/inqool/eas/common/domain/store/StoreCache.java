package cz.inqool.eas.common.domain.store;

import cz.inqool.eas.common.domain.Domain;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Caching for store instances for different projections.
 */
@CacheConfig(cacheNames={"stores"})
@Service
public class StoreCache {

    /**
     * Creates and caches projection store instance with specified type.
     *
     * @param store Root store instance
     * @param projectedType Projection type
     * @param <ROOT> Root of the projection type system
     * @param <PROJECTED> Projection type
     *
     * @return New/Cached projection store
     */
    @Cacheable
    public <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> DomainStore<ROOT, PROJECTED, ?> get(DomainStore<ROOT, ?, ?> store, Class<PROJECTED> projectedType) {
        return store.getProjection(projectedType);
    }
}
