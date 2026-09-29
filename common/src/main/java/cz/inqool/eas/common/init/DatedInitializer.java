package cz.inqool.eas.common.init;

import com.google.common.collect.Sets;
import cz.inqool.eas.common.dated.Dated;
import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.store.DatedStore;
import cz.inqool.eas.common.domain.Domain;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class DatedInitializer<
        ROOT extends Dated<ROOT>,
        REPOSITORY extends DatedRepository<ROOT, ?, ?, ?, ?>
        > extends DomainInitializer<
        ROOT,
        REPOSITORY> {

    /**
     * Save given {@code entities} using {@code repository}.
     * Restores all existing entities that were previously deleted by user.
     */
    @Override
    protected void save(Collection<? extends ROOT> entities) {
        log.debug("Creating {} entries...", entities.size());

        REPOSITORY repository = getRepository();
        DatedStore<ROOT, ROOT, ?> store = repository.getStore();

        Map<String, ROOT> restoredEntities = new HashMap<>();
        List<String> deletedEntityIds = new ArrayList<>();
        for (ROOT entity : entities) {
            if (store.isDeleted(entity.getId())) {
                log.debug("Restoring entity {}...", entity);
                ROOT restoredEntity = store.restore(entity.getId());
                restoredEntities.put(restoredEntity.getId(), restoredEntity);
            }
            if (entity.getDeleted() != null) {
                deletedEntityIds.add(entity.getId());
            }
        }

        entities = entities.stream()
                .map(entity -> restoredEntities.getOrDefault(entity.getId(), entity)) // need to replace when restored because of entity version update
                .collect(Collectors.toSet());
        persist(entities, repository);
        repository.delete(deletedEntityIds);
    }

    @Override
    protected void persist(Collection<? extends ROOT> entities, REPOSITORY repository) {
        repository.update(entities);
    }

    @Override
    protected void deleteOthers(Collection<? extends ROOT> others) {
        deleteOthers(others, true);
    }

    /**
     * Deletes all entities NOT in the supplied set.
     */
    protected void deleteOthers(Collection<? extends ROOT> others, boolean permanent) {
        REPOSITORY repository = getRepository();

        Set<String> otherIds = others.stream().map(Domain::getId).collect(Collectors.toSet());
        Collection<ROOT> all = repository.listAll();
        Set<String> allIds = all.stream().map(Domain::getId).collect(Collectors.toSet());

        Sets.SetView<String> toRemoveIds = Sets.difference(allIds, otherIds);
        if (toRemoveIds.size() > 0) {
            log.debug("Deleting {} entries...", toRemoveIds.size());
        } else {
            log.debug("Nothing to delete.");
        }

        if (permanent) {
            repository.deletePermanently(toRemoveIds);
        } else {
            repository.delete(toRemoveIds);
        }
    }

    public static <REF_ROOT extends Domain<REF_ROOT>, TYPE extends REF_ROOT> TYPE ref(String id, Class<TYPE> type) {
        TYPE ref = newInstance(type);
        ref.setId(id);
        return ref;
    }

    public static <REF_ROOT extends Domain<REF_ROOT>, TYPE extends REF_ROOT> Set<TYPE> refs(Set<String> ids, Class<TYPE> type) {
        return ids
                .stream()
                .map(id -> {
                    TYPE ref = newInstance(type);
                    ref.setId(id);
                    return ref;
                })
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    protected static <TARGET extends Domain<?>> TARGET newInstance(Class<TARGET> targetClass) {
        Constructor<? extends TARGET> constructor = targetClass.getDeclaredConstructor();
        return constructor.newInstance();
    }

    @Override
    public void initializeBackward() throws Exception {
        if (isOnlyIfEmpty()) {
            if (getRepository().countAll() > 0) {
                log.info("Skipping backward initialization...");
                return;
            }
        }

        if (isRemoveOthers()) {
            deleteOthers(entities, isRemovePermanently());
        }

        this.entities = null;
    }

    protected boolean isRemovePermanently() {
        return false;
    }
}
