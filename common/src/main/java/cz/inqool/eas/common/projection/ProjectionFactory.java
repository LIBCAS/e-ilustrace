package cz.inqool.eas.common.projection;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Projection factory based on entity-views and index projections.
 * <p>
 * Can be replaced by different implementation extending and overriding this class.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Service
@CacheConfig(cacheNames = {"projections"})
public class ProjectionFactory {
    @Cacheable
    public <ROOT extends Projectable<ROOT>, BASE extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>> Projection<ROOT, BASE, PROJECTED> get(Class<BASE> baseType, Class<PROJECTED> projectedType) {
        if (baseType.equals(projectedType) || baseType.isAssignableFrom(projectedType)) {
            return (Projection<ROOT, BASE, PROJECTED>) new SelfProjection<ROOT>();
        } else if (Indexed.class.isAssignableFrom(projectedType)) {
            return new IndexableProjection(projectedType);
        } else {
            return (Projection<ROOT, BASE, PROJECTED>) new ViewProjection<>((Class<ROOT>) baseType, projectedType);
        }
    }
}
