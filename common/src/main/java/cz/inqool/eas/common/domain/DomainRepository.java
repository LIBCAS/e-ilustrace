package cz.inqool.eas.common.domain;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.jpa.impl.JPAQuery;
import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.domain.index.DomainIndex;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.domain.store.DomainStore;
import cz.inqool.eas.common.domain.store.StoreCache;
import cz.inqool.eas.common.exception.GeneralException;
import cz.inqool.eas.common.module.ModuleDefinition;
import cz.inqool.eas.common.module.Modules;
import cz.inqool.eas.common.projection.Projection;
import cz.inqool.eas.common.projection.ProjectionFactory;
import cz.inqool.eas.common.utils.CollectionUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.jodah.typetools.TypeResolver;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repository for objects implementing {@link Domain}.
 * <p>
 * Database and index functionality combined together.
 *
 * @param <ROOT>            Root of the projection type system
 * @param <INDEX_PROJECTED> Index projection type
 * @param <INDEXED>         Indexed object type
 * @param <STORE>           Type of store
 * @param <INDEX>           Type of index
 */
@Slf4j
public abstract class DomainRepository<
        ROOT extends Domain<ROOT>,
        INDEX_PROJECTED extends Domain<ROOT>,
        INDEXED extends DomainIndexed<ROOT, INDEX_PROJECTED>,
        STORE extends DomainStore<ROOT, ROOT, ?>,
        INDEX extends DomainIndex<ROOT, INDEX_PROJECTED, INDEXED>
        > {
    protected ProjectionFactory projectionFactory;

    private StoreCache stores;

    protected ApplicationContext applicationContext;

    protected EventBuilder eventBuilder;

    protected EventService eventService;

    protected STORE store;

    protected INDEX index;

    @Getter
    protected Class<ROOT> rootType;

    protected Class<INDEX_PROJECTED> indexProjectedType;

    protected IndexUpdateStrategy defaultIndexUpdateStrategy = IndexUpdateStrategy.CONVERT_TO_INDEX_VIEW;

    @Getter
    protected Class<INDEXED> indexableType;

    /**
     * Initializes types, projections and inject store and index bean.
     * <p>
     * Should be overridden only in case the types are not derivable from type arguments.
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    protected void postInit() {
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(DomainRepository.class, getClass());

        this.rootType = (Class<ROOT>) typeArguments[0];
        this.indexProjectedType = (Class<INDEX_PROJECTED>) typeArguments[1];
        this.indexableType = (Class<INDEXED>) typeArguments[2];
        this.store = constructStore((Class<STORE>) typeArguments[3], rootType);
        this.index = constructIndex((Class<INDEX>) typeArguments[4], indexableType);

        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(this.store);
        beanFactory.autowireBean(this.index);

        if (this.isIndexManaged()) {
            this.index.initializeIndexedFields();
        }
    }

    /**
     * Creates new object in database and indexes it.
     *
     * @param object      Object to create
     * @param <PROJECTED> Type of projection
     * @return Created object in detached state
     */
    @SuppressWarnings("unchecked")
    public <PROJECTED extends Domain<ROOT>> PROJECTED create(@NotNull PROJECTED object) {
        Class<PROJECTED> type = (Class<PROJECTED>) object.getClass();

        object = getStore(type).create(object);

        switch (defaultIndexUpdateStrategy) {
            case CONVERT_TO_INDEX_VIEW:
                INDEXED indexed = projectedToIndexable(type, object);
                index.index(indexed);
                break;
            case USE_INDEX_PROJECTION_FETCH:
                reindexInternal(object.getId());
                break;
            default:
                throw new IllegalStateException("Strategy '" + defaultIndexUpdateStrategy + "' not supported.");
        }

        return object;
    }

    /**
     * Creates collection of objects in a batch and indexes them.
     * <p>
     * The objects should be of the same type.
     *
     * @param objects     Collection of objects to create
     * @param <PROJECTED> Type of projection
     * @return Collection of created objects
     */
    @SuppressWarnings("unchecked")
    public <PROJECTED extends Domain<ROOT>> Collection<? extends PROJECTED> create(@NotNull Collection<? extends PROJECTED> objects) {
        if (objects.size() == 0) {
            return objects;
        }

        // FIXME: Collection<Document> can contain different types: DeliveredDocument, OutgoingDocument ...
        Class<PROJECTED> type = (Class<PROJECTED>) objects.iterator().next().getClass();

        objects = getStore(type).create(objects);

        switch (defaultIndexUpdateStrategy) {
            case CONVERT_TO_INDEX_VIEW:
                List<INDEXED> indexed = projectedToIndexable(type, objects);
                index.index(indexed);
                break;
            case USE_INDEX_PROJECTION_FETCH:
                List<String> ids = objects.stream()
                        .map(Domain::getId)
                        .collect(Collectors.toList());
                reindexInternal(ids);
                break;
            default:
                throw new IllegalStateException("Strategy '" + defaultIndexUpdateStrategy + "' not supported.");
        }

        return objects;
    }

    /**
     * Updates given object in database and index.
     *
     * @param object      Object to update
     * @param <PROJECTED> Type of projection
     * @return Saved object
     */
    @SuppressWarnings("unchecked")
    public <PROJECTED extends Domain<ROOT>> PROJECTED update(@NotNull PROJECTED object) {
        Class<PROJECTED> type = (Class<PROJECTED>) object.getClass();

        object = getStore(type).update(object);

        switch (defaultIndexUpdateStrategy) {
            case CONVERT_TO_INDEX_VIEW:
                INDEXED indexed = projectedToIndexable(type, object);
                index.index(indexed);
                break;
            case USE_INDEX_PROJECTION_FETCH:
                reindexInternal(object.getId());
                break;
            default:
                throw new IllegalStateException("Strategy '" + defaultIndexUpdateStrategy + "' not supported.");
        }

        return object;
    }


    /**
     * Updates given collection of objects in a batch in database and index.
     * <p>
     * The objects should be of the same type.
     *
     * @param objects     Collection of objects to update
     * @param <PROJECTED> Type of projection
     * @return collection of updated objects
     */
    @SuppressWarnings("unchecked")
    public <PROJECTED extends Domain<ROOT>> Collection<? extends PROJECTED> update(@NotNull Collection<? extends PROJECTED> objects) {
        if (objects.size() == 0) {
            return objects;
        }

        // FIXME: Collection<Document> can contain different types: DeliveredDocument, OutgoingDocument ...
        Class<PROJECTED> type = (Class<PROJECTED>) objects.iterator().next().getClass();

        objects = getStore(type).update(objects);

        switch (defaultIndexUpdateStrategy) {
            case CONVERT_TO_INDEX_VIEW:
                List<INDEXED> indexed = projectedToIndexable(type, objects);
                index.index(indexed);
                break;
            case USE_INDEX_PROJECTION_FETCH:
                List<String> ids = objects.stream()
                        .map(Domain::getId)
                        .collect(Collectors.toList());
                reindexInternal(ids);
                break;
            default:
                throw new IllegalStateException("Strategy '" + defaultIndexUpdateStrategy + "' not supported.");
        }

        return objects;
    }

    /**
     * Deletes given object from database and index.
     *
     * @param id Id of object to delete
     * @return Resultant entity or {@code null} if the entity was not found
     */
    public ROOT delete(@NotNull String id) {
        INDEX_PROJECTED projected = getStore(indexProjectedType).delete(id);

        if (projected != null) {
            INDEXED indexed = projectedToIndexable(indexProjectedType, projected);
            index.delete(indexed);

            if (eventService != null) {
                eventService.create(eventBuilder.deleting(projected, UserGenerator.generateValue(), getModule()));
            }
        }

        Projection<ROOT, ROOT, INDEX_PROJECTED> projection = projectionFactory.get(rootType, indexProjectedType);
        return projection.toBase(projected);
    }

    /**
     * Deletes given collection of objects in a batch from database and index.
     *
     * @param ids Collection of ids of objects to delete
     * @return Collection of deleted objects
     */
    public Collection<ROOT> delete(@NotNull Collection<String> ids) {
        DomainStore<ROOT, INDEX_PROJECTED, ?> store = getStore(indexProjectedType);
        Projection<ROOT, ROOT, INDEX_PROJECTED> projection = projectionFactory.get(rootType, indexProjectedType);

        int batchSize = 100;
        return CollectionUtils.partition(ids, batchSize).stream()
                .map(store::delete)
                .peek(projected -> {
                    List<INDEXED> indexed = projectedToIndexable(indexProjectedType, projected);
                    index.delete(indexed);
                })
                .flatMap(Collection::stream)
                .map(projection::toBase)
                .collect(Collectors.toList());
    }

    /**
     * Finds all objects by their IDs
     *
     * @param ids list of IDs
     */
    public List<ROOT> list(@NotNull Collection<String> ids) {
        return list(rootType, ids);
    }

    /**
     * Finds all objects by their IDs
     *
     * @param type type of projection
     * @param ids collection of IDs
     * @param <PROJECTED> type of projection
     */
    public <PROJECTED extends Domain<ROOT>> List<PROJECTED> list(@NotNull Class<PROJECTED> type, @NotNull Collection<String> ids) {
        return getStore(type).listByIds(List.copyOf(ids));
    }

    /**
     * Finds all objects that respect the selected {@link Params} using the public result extractor.
     * <p>
     * Through {@link Params} one could specify filtering, sorting and paging. The returned result also contains the
     * total number of objects passing through the filtering phase.
     *
     * @param params Parameters to comply with
     * @return Sorted list of objects with total number
     */
    public Result<ROOT> listByParams(@NotNull Params params) {
        return listByParams(rootType, params);
    }

    /**
     * Finds all objects that respect the selected {@link Params} using the public result extractor.
     * <p>
     * Through {@link Params} one could specify filtering, sorting and paging. The returned result also contains the
     * total number of objects passing through the filtering phase.
     *
     * @param type        Provided projection
     * @param params      Parameters to comply with
     * @param <PROJECTED> Type of projection
     * @return Sorted list of objects with total number
     */
    public <PROJECTED extends Domain<ROOT>> Result<PROJECTED> listByParams(Class<PROJECTED> type, @NotNull Params params) {
        Result<String> idsResult = index.listIdsByParams(params);
        List<PROJECTED> items = getStore(type).listByIds(idsResult.getItems());

        return new Result<>(items, idsResult.getCount(), idsResult.getSearchAfter(), idsResult.getAggregations());
    }

    /**
     * @see DomainIndex#listAllIdsByParams(Params)
     */
    public List<String> listAllIdsByParams(@NotNull Params params) {
        return index.listAllIdsByParams(params);
    }

    /**
     * Recreates index for all objects from Store to Index using index projection.
     */
    public void reindex() {
        index.initIndex();

        long itemCount = store.countAll();
        log.info("Indexing {} objects.", itemCount);

        long batchSize = getReindexBatchSize();
        AtomicLong counter = new AtomicLong();

        List<INDEX_PROJECTED> projected = new ArrayList<>();
        DomainStore<ROOT, INDEX_PROJECTED, ?> store = getStore(indexProjectedType);

        Runnable indexFunction = () -> {
            index.index(projectedToIndexable(indexProjectedType, projected));
            counter.addAndGet(projected.size());
            log.debug("Indexed {}/{} objects.", counter, itemCount);

            projected.clear();
            store.detachAll();
        };

        try (CloseableIterator<INDEX_PROJECTED> iterator = store.iterateAll(query -> query
                .setHint(QueryHints.HINT_READONLY, true)
                .setHint(QueryHints.HINT_FETCH_SIZE, (int) batchSize)
        )) {
            while (iterator.hasNext()) {
                projected.add(iterator.next());

                if (projected.size() >= batchSize) {
                    indexFunction.run();
                }
            }

            if (!projected.isEmpty()) {
                indexFunction.run();
            }
        }
    }

    /**
     * Reindexes items with ids in given list using index projection.
     */
    public void reindex(Collection<String> ids) {
        if (!isIndexInitialized()) {
            index.initIndex();
        }

        long itemCount = ids.size();
        log.debug("Indexing {} objects.", itemCount);

        long counter = 0;

        DomainStore<ROOT, INDEX_PROJECTED, ?> store = getStore(indexProjectedType);
        for (List<String> idsBatch : CollectionUtils.partition(ids, getReindexBatchSize())) {
            Collection<INDEX_PROJECTED> projected = store.listByIds(idsBatch, store::list);
            Collection<INDEXED> indexed = new ArrayList<>(projectedToIndexable(indexProjectedType, projected));
            index.index(indexed);

            counter += indexed.size();
            log.debug("Indexed {}/{} objects.", counter, itemCount);
        }
    }

    private void reindexInternal(String id) {
        DomainStore<ROOT, INDEX_PROJECTED, ?> store = getStore(indexProjectedType);

        INDEX_PROJECTED projected = store.find(id);
        INDEXED indexed = projectedToIndexable(indexProjectedType, projected);
        index.index(indexed);
    }

    private void reindexInternal(List<String> ids) {
        DomainStore<ROOT, INDEX_PROJECTED, ?> store = getStore(indexProjectedType);

        for (List<String> idsBatch : CollectionUtils.partition(ids, getReindexBatchSize())) {
            Collection<INDEX_PROJECTED> projected = store.listByIds(idsBatch, store::list);
            Collection<INDEXED> indexed = new ArrayList<>(projectedToIndexable(indexProjectedType, projected));
            index.index(indexed);
        }
    }

    /**
     * Gets maximum number of object in one reindex batch.
     * <p>
     * Can be overridden in subclass to increase or decrease based on memory pressure.
     */
    public int getReindexBatchSize() {
        return 10000;
    }


    /**
     * Tests if index is initialized.
     */
    public boolean isIndexInitialized() {
        return index.isIndexInitialized();
    }

    /**
     * Marks if the index should be managed by EAS (auto creating index).
     * <p>
     * Only in specific cases should be this method overridden.
     */
    public boolean isIndexManaged() {
        return true;
    }

    /**
     * Initializes index.
     */
    public void initIndex() {
        index.initIndex();
    }

    /**
     * Drops index.
     */
    public void dropIndex() {
        index.dropIndex();
    }

    /**
     * @see DomainStore#exist(String)
     */
    public boolean exist(@NotNull String id) {
        return getStore().exist(id);
    }

    /**
     * @see DomainStore#find(String)
     */
    public ROOT find(@NotNull String id) {
        return find(rootType, id);
    }

    /**
     * @see DomainStore#getRef(String)
     */
    public ROOT getRef(@NotNull String id) {
        return getRef(rootType, id);
    }

    /**
     * @see DomainStore#find(String)
     */
    public <PROJECTED extends Domain<ROOT>> PROJECTED find(Class<PROJECTED> type, @NotNull String id) {
        return getStore(type).find(id);
    }

    /**
     * @see DomainStore#getRef(String)
     */
    public <PROJECTED extends Domain<ROOT>> PROJECTED getRef(Class<PROJECTED> type, String id) {
        return getStore(type).getRef(id);
    }

    /**
     * @see DomainStore#listAll()
     */
    public Collection<ROOT> listAll() {
        return listAll(rootType);
    }

    /**
     * @see DomainStore#listAll()
     */
    public <PROJECTED extends Domain<ROOT>> Collection<PROJECTED> listAll(Class<PROJECTED> type) {
        return getStore(type).listAll();
    }

    /**
     * @see DomainStore#listByWindow(long, long)
     */
    public Collection<ROOT> listAll(long offset, long limit) {
        return listAll(rootType, offset, limit);
    }

    /**
     * @see DomainStore#listByWindow(long, long)
     */
    public <PROJECTED extends Domain<ROOT>> Collection<PROJECTED> listAll(Class<PROJECTED> type, long offset, long limit) {
        return getStore(type).listByWindow(offset, limit);
    }

    /**
     * @see DomainStore#countAll()
     */
    public long countAll() {
        return getStore().countAll();
    }

    /**
     * @see DomainIndex#countByParams(Params)
     */
    public long countByParams(@NotNull Params params) {
        return getIndex().countByParams(params);
    }

    /**
     * @see DomainStore#query()
     */
    public JPAQuery<?> query() {
        return getStore().query();
    }

    /**
     * @see DomainStore#detachAll()
     */
    protected void detachAll() {
        getStore().detachAll();
    }

    /**
     * Retrieves projection store based on provided projected type.
     *
     * @param type        Projection type class
     * @param <PROJECTED> Projection type
     * @return projection store
     */
    public <PROJECTED extends Domain<ROOT>> DomainStore<ROOT, PROJECTED, ?> getStore(Class<PROJECTED> type) {
        return stores.get(this.store, type);
    }

    /**
     * Retrieves main store.
     *
     * @return Main store
     */
    @SuppressWarnings("unchecked")
    public STORE getStore() {
        return this.store;
    }

    /**
     * Returns index.
     */
    public INDEX getIndex() {
        return index;
    }

    protected <PROJECTED extends Domain<ROOT>> INDEXED projectedToIndexable(Class<PROJECTED> type, PROJECTED projected) {
        Projection<ROOT, ROOT, PROJECTED> entityProjection = projectionFactory.get(rootType, type);
        Projection<ROOT, ROOT, INDEX_PROJECTED> indexProjection = projectionFactory.get(rootType, indexProjectedType);
        Projection<ROOT, INDEX_PROJECTED, INDEXED> indexedProjection = projectionFactory.get(indexProjectedType, indexableType);

        ROOT entity = entityProjection.toBase(projected);
        INDEX_PROJECTED indexProjected = indexProjection.toProjected(entity);
        return indexedProjection.toProjected(indexProjected);
    }

    protected <PROJECTED extends Domain<ROOT>> List<INDEXED> projectedToIndexable(Class<PROJECTED> type, Collection<? extends PROJECTED> projected) {
        Projection<ROOT, ROOT, PROJECTED> entityProjection = projectionFactory.get(rootType, type);
        Projection<ROOT, ROOT, INDEX_PROJECTED> indexProjection = projectionFactory.get(rootType, indexProjectedType);
        Projection<ROOT, INDEX_PROJECTED, INDEXED> indexedProjection = projectionFactory.get(indexProjectedType, indexableType);

        return projected.
                stream().
                map(entityProjection::toBase).
                map(indexProjection::toProjected).
                map(indexedProjection::toProjected).
                collect(Collectors.toList());
    }

    protected STORE constructStore(Class<STORE> storeType, Class<ROOT> type) {
        try {
            for (Constructor<?> constructor : storeType.getDeclaredConstructors()) {
                if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].equals(Class.class)) {
                    constructor = storeType.getDeclaredConstructor(Class.class);
                    return (STORE) constructor.newInstance(type);
                }
                else if (constructor.getParameterCount() == 0) {
                    constructor = storeType.getDeclaredConstructor();
                    return (STORE) constructor.newInstance();
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new GeneralException(e);
        } catch (NoSuchMethodException e) {
            throw new GeneralException("Can not construct store instance.");
        }
        throw new GeneralException("Can not construct store instance.");
    }

    protected INDEX constructIndex(Class<INDEX> indexType, Class<INDEXED> indexedType) {
        try {
            for (Constructor<?> constructor : indexType.getDeclaredConstructors()) {
                if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].equals(Class.class)) {
                    constructor = indexType.getDeclaredConstructor(Class.class);
                    return (INDEX) constructor.newInstance(indexedType);
                }
                else if (constructor.getParameterCount() == 0) {
                    constructor = indexType.getDeclaredConstructor();
                    return (INDEX) constructor.newInstance();
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new GeneralException(e);
        } catch (NoSuchMethodException e) {
            throw new GeneralException("Can not construct index instance.");
        }
        throw new GeneralException("Can not construct index instance.");
    }

    /**
     * Returns module this repository belongs to.
     * <p>
     * Override this method to specify custom module.
     */
    protected ModuleDefinition getModule() {
        return Modules.EAS;
    }

    @Autowired
    public void setProjectionFactory(ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
    }

    @Autowired
    public void setStores(StoreCache stores) {
        this.stores = stores;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired(required = false)
    public void setEventBuilder(EventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
    }

    @Autowired(required = false)
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Set injected value of index update strategy. Override this method in repository subclass (with {@link Autowired}
     * annotation) when need to specify different strategy only on some repositories.
     */
    @Autowired
    public void setDefaultIndexUpdateStrategy(@Value("${eas.repository.index.update-strategy:CONVERT_TO_INDEX_VIEW}") IndexUpdateStrategy strategy) {
        this.defaultIndexUpdateStrategy = strategy;
    }


    /**
     * Specifies indexing strategy of entries during create/update operations.
     */
    public enum IndexUpdateStrategy {

        /**
         * Converts created/updated object projection to index projection and uses it for reindexing. Possible loss of
         * data in case when projection does not contain all data that index projection requires. Suitable when using
         * only ROOT type instances for create/update operations, as it is faster.
         */
        CONVERT_TO_INDEX_VIEW,

        /**
         * Fetches data for index projection separately from DB during create/update operations, ignores create/update
         * projection when indexing data. Suitable when using custom (non-root) projections for create/update.
         */
        USE_INDEX_PROJECTION_FETCH
    }
}
