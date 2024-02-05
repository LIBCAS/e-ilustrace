package cz.inqool.eas.common.domain.index.reindex.reference;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This component caches all index references on index classes for all entities on application startup to provide them
 * during entity updating.
 */
@Slf4j
public class IndexReferenceHolder {

    private final Map<Class<? extends Domain<?>>, Set<IndexReferenceField>> entityUsageMap = new ConcurrentHashMap<>();


    /**
     * Register given references.
     */
    public void registerReferences(Set<IndexReferenceField> references) {
        for (IndexReferenceField indexReference : references) {
            Class<? extends Domain<?>> referencedClass = indexReference.getReferencedClass();

            Set<Class<? extends Domain<?>>> targetClasses = new LinkedHashSet<>();
            targetClasses.add(referencedClass);
            targetClasses.addAll(ReflectionUtils.getSubTypesOf(referencedClass));
            // todo maybe register entity view classes

            for (Class<? extends Domain<?>> targetClass : targetClasses) {
                entityUsageMap.computeIfAbsent(targetClass, aClass -> new LinkedHashSet<>())
                        .add(indexReference);
            }
        }
        log.info("Registered {} index references ({} map entries)", references.size(), entityUsageMap.keySet().size());
    }

    /**
     * Get a set of all index references for given {@code entityType}.
     */
    public Set<IndexReferenceField> getReferences(Class<? extends Domain<?>> entityType) {
        return entityUsageMap.getOrDefault(entityType, Collections.emptySet());
    }
}
