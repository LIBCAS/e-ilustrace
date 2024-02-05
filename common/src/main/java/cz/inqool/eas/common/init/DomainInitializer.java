package cz.inqool.eas.common.init;

import com.google.common.collect.Sets;
import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.DomainRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class DomainInitializer<
        ROOT extends Domain<ROOT>,
        REPOSITORY extends DomainRepository<ROOT, ?, ?, ?, ?>
        > implements DataInitializer {

    protected List<ROOT> entities;

    /**
     * Save given {@code entities} using {@code repository}.
     */
    protected void save(Collection<? extends ROOT> entities) {
        log.debug("Creating {} entries...", entities.size());

        REPOSITORY repository = getRepository();

        persist(entities, repository);
    }

    protected void persist(Collection<? extends ROOT> entities, REPOSITORY repository) {
        repository.update(entities);
    }

    /**
     * Deletes all entities NOT in the supplied set.
     */
    protected void deleteOthers(Collection<? extends ROOT> others) {
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

        repository.delete(toRemoveIds);
    }

    protected <TYPE extends ROOT> TYPE findOrDefault(String id, Class<TYPE> type) {
        TYPE entity = getRepository().find(type, id);
        if (entity == null) {
            entity = newInstance(type);
            entity.setId(id);
        }
        return entity;
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
    public void initialize() throws Exception {
        if (isOnlyIfEmpty()) {
            if (getRepository().countAll() > 0) {
                log.info("Skipping initialization...");
                return;
            }
        }

        this.entities = initializeEntities();

        save(entities);
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
            deleteOthers(entities);
        }

        this.entities = null;
    }

    protected abstract List<ROOT> initializeEntities() throws Exception;

    protected abstract REPOSITORY getRepository();

    protected boolean isOnlyIfEmpty() {
        return false;
    }

    protected boolean isRemoveOthers() {
        return false;
    }

}
