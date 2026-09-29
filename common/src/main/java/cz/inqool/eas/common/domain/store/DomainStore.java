package cz.inqool.eas.common.domain.store;

import com.google.common.collect.Lists;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.ScrollableResultsIterator;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import cz.inqool.eas.common.differ.DifferModule;
import cz.inqool.eas.common.differ.DifferModuleState;
import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.event.store.*;
import cz.inqool.eas.common.domain.store.list.ListFunction;
import cz.inqool.eas.common.domain.store.list.QueryModifier;
import cz.inqool.eas.common.exception.GeneralException;
import lombok.Getter;
import lombok.SneakyThrows;
import net.jodah.typetools.TypeResolver;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.AssertionUtils.gte;
import static cz.inqool.eas.common.utils.CollectionUtils.sortByIds;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

/**
 * Database store for objects extending {@link DomainObject} with standard CRUD operations.
 *
 * @param <ROOT>       Root of the projection type system
 * @param <PROJECTED>  Projection type
 * @param <META_MODEL> Meta model for projection type
 */
public class DomainStore<ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>, META_MODEL extends EntityPathBase<PROJECTED>> {
    protected EntityManager entityManager;

    protected ApplicationEventPublisher eventPublisher;

    protected DifferModule differModule;

    @Getter
    protected JPAQueryFactory queryFactory;

    @Getter
    private final Class<? extends PROJECTED> type;

    @Getter
    private final Class<? extends ROOT> rootType;

    @Getter
    protected final META_MODEL metaModel;

    protected final QDomainObject domainMetaModel;

    public DomainStore(Class<? extends PROJECTED> type) {
        this.type = type;
        this.rootType = deriveRootType(type);
        this.metaModel = deriveMetaModel(type);
        this.domainMetaModel = new QDomainObject(metaModel.getMetadata());
    }

    /**
     * Clones the store instance with different projection.
     *
     * Use {@link StoreCache#get(DomainStore, Class)} for caching the instances.
     *
     * @param <OTHER_PROJECTED> Another projected type
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <OTHER_PROJECTED extends Domain<ROOT>> DomainStore<ROOT, OTHER_PROJECTED, ?> getProjection(Class<OTHER_PROJECTED> type) {
        if (this.type == type) {
            return (DomainStore<ROOT, OTHER_PROJECTED, ?>) this;
        }
        try {
            Constructor<?> constructor = Arrays.stream(this.getClass().getDeclaredConstructors())
                    .filter(declaredConstructor -> declaredConstructor.getParameterCount() == 1)
                    .filter(declaredConstructor -> declaredConstructor.getParameterTypes()[0].equals(Class.class))
                    .findAny()
                    .orElseThrow(() -> new NoSuchElementException(format("No constructor found for type '%s' with one Class parameter", this.getClass())));

            DomainStore projectionStore = (DomainStore) constructor.newInstance(type);
            projectionStore.setEntityManager(this.entityManager);
            projectionStore.setEventPublisher(this.eventPublisher);

            projectionStore.setDifferModuleAndProcessFields(this.differModule);

            return projectionStore;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Can not find suitable Metamodel class.", e);
        }
    }

    /**
     * Creates new object in database.
     *
     * @param entity object to create
     * @return created object in detached state
     */
    public PROJECTED create(@NotNull PROJECTED entity) {
        if (differModule != null && DifferModuleState.isEnabled()) {
            eventPublisher.publishEvent(new StorePreCreateEvent<>(this, entity));
        }

        PROJECTED saved = entityManager.merge(entity);

        entityManager.flush();
        detachAll();

        if (differModule != null && DifferModuleState.isEnabled()) {
            eventPublisher.publishEvent(new StorePostCreateEvent<>(this, saved));
        }

        return saved;
    }

    /**
     * Creates collection of objects in a batch.
     *
     * @param entities collection of objects to create
     * @return collection of created objects
     */
    public Collection<? extends PROJECTED> create(@NotNull Collection<? extends PROJECTED> entities) {
        if (entities.isEmpty()) {
            return emptyList();
        }

        if (differModule != null && DifferModuleState.isEnabled()) {
            eventPublisher.publishEvent(new StorePreCreateCollectionEvent<>(this, entities));
        }

        Set<? extends PROJECTED> saved = entities.stream()
                .map(entityManager::merge)
                .collect(Collectors.toSet());

        entityManager.flush();
        detachAll();

        if (differModule != null && DifferModuleState.isEnabled()) {
            eventPublisher.publishEvent(new StorePostCreateCollectionEvent<>(this, saved));
        }

        return saved;
    }

    /**
     * Updates given object.
     *
     * @param entity object to update
     * @return saved object
     */
    public PROJECTED update(@NotNull PROJECTED entity) {
        PROJECTED oldEntity = null;
        if (differModule != null && DifferModuleState.isEnabled()) {
            oldEntity = find(entity.getId());
            eventPublisher.publishEvent(new StorePreUpdateEvent<>(this, oldEntity, entity));
        }

        PROJECTED saved = entityManager.merge(entity);

        entityManager.flush();
        detachAll();

        if (differModule != null && DifferModuleState.isEnabled()) {
            eventPublisher.publishEvent(new StorePostUpdateEvent<>(this, oldEntity, saved));
        }

        return saved;
    }

    /**
     * Updates given collection of objects in a batch.
     *
     * @param entities collection of objects to update
     * @return collection of updated objects
     */
    public Collection<? extends PROJECTED> update(@NotNull Collection<? extends PROJECTED> entities) {
        if (entities.isEmpty()) {
            return emptyList();
        }

        List<PROJECTED> oldEntities = null;
        if (differModule != null && DifferModuleState.isEnabled()) {
            List<String> ids = entities.stream().map(Domain::getId).collect(Collectors.toList());
            oldEntities = listByIds(ids);

            eventPublisher.publishEvent(new StorePreUpdateCollectionEvent<>(this, oldEntities, entities));
        }


        Set<? extends PROJECTED> saved = entities.stream()
                .map(entityManager::merge)
                .collect(Collectors.toSet());

        entityManager.flush();
        detachAll();

        if (differModule != null && DifferModuleState.isEnabled()) {
            eventPublisher.publishEvent(new StorePostUpdateCollectionEvent<>(this, oldEntities, saved));
        }

        return saved;
    }

    /**
     * Returns the number of stored objects.
     */
    public long countAll() {
        return query().
                select(metaModel).
                from(metaModel).
                fetchCount();
    }

    /**
     * Detaches all objects from JPA context.
     */
    public void detachAll() {
        entityManager.clear();
    }

    /**
     * Creates QueryDSL query object.
     */
    public JPAQuery<?> query() {
        return queryFactory.query();
    }

    /**
     * Creates QueryDSL delete query object.
     */
    public JPADeleteClause deleteQuery() {
        return queryFactory.delete(metaModel);
    }

    /**
     * Creates QueryDSL update query object.
     */
    public JPAUpdateClause updateQuery() {
        return queryFactory.update(metaModel);
    }

    /**
     * Returns object with given ID.
     *
     * @param id ID of object to return
     * @return found object or {@code null} if not found
     */
    public PROJECTED find(@NotNull String id) {
        PROJECTED entity = findConnected(id);

        if (entity != null) {
            detachAll();
        }

        return entity;
    }

    /**
     * Checks whether object with given ID exists in database.
     *
     * @param id ID of object
     * @return {@code true} if object exists, {@code false} otherwise
     */
    public boolean exist(@NotNull String id) {
        long count = query().
                select(domainMetaModel.id).
                from(metaModel).
                where(domainMetaModel.id.eq(id)).
                fetchCount();

        return count > 0;
    }

    /**
     * Returns objects using query modifier.
     *
     * @param modifier Query modifier
     */
    public final List<PROJECTED> list(QueryModifier<ROOT, PROJECTED> modifier) {
        JPAQuery<PROJECTED> query = query().
                select(metaModel).
                from(metaModel);

        modifier.modify(query);

        // need to define ordering to not retrieve records in random order
        // (and same records returned multiple times in separate calls).
        query.orderBy(defaultListAllOrder());

        List<PROJECTED> list = query.fetch();

        detachAll();

        return list;
    }

    /**
     * Returns objects using query modifier.
     *
     * Difference from {@link DomainStore#list(QueryModifier)} is that
     * it can be overridden in subclasses to add default modifier to all list-like methods.
     *
     * @param modifier Query modifier
     */
    public List<PROJECTED> listDefault(QueryModifier<ROOT, PROJECTED> modifier) {
        return list(modifier);
    }

    /**
     * Finds the objects corresponding to the specified list of IDs.
     *
     * @param ids list of IDs
     * @return collection of found instances
     */
    public List<PROJECTED> listByIds(@NotNull List<String> ids, ListFunction<ROOT, PROJECTED> listFunction) {
        if (ids.isEmpty()) {
            return emptyList();
        }

        int maxParameterCountLimit = 1000; // DBMS have limits for parameter count, 1K is used as a practical value
        List<PROJECTED> list = Lists.partition(ids, maxParameterCountLimit).stream()
                .map(idsBatch -> listFunction.call((query) -> query.where(domainMetaModel.id.in(idsBatch))))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(ArrayList::new));

        return sortByIds(ids, list, Domain::getId);
    }

    /**
     * Returns objects in batches.
     *
     * @param offset defines the offset for the query results
     * @param limit  defines the limit / max results for the query results
     * @return collection of found instances
     */
    public List<PROJECTED> listByWindow(long offset, long limit, ListFunction<ROOT, PROJECTED> listFunction) {
        gte(offset, 0L, () -> new IllegalArgumentException("Offset must be a non-negative number."));
        gte(limit, 0L, () -> new IllegalArgumentException("Limit must be a non-negative number."));

        return listFunction.call((query) -> {
            if (offset != 0) {
                query.offset(offset);
            }

            if (limit != 0) {
                query.limit(limit);
            }
        });
    }

    /**
     * @see #iterateAll(QueryModifier)
     */
    public CloseableIterator<PROJECTED> iterateAll() {
        return iterateAll(query -> {});
    }

    /**
     * Iterate over all instances using scrolling.
     * <p>
     * Do not forget to close this iterator to release resources for cursor used within.
     * <p>
     * Use {@link QueryHints} in modifier to optimize query when iterating over large amount of data (especially
     * {@link QueryHints#HINT_FETCH_SIZE})
     * <p>
     * This method is more suitable when there are a lot of instances (more than 100K) because fetching via offset is
     * slower for farther results from the start as every result before must be skipped.
     * <p>
     * Tested on {@code PostgreSQL 11} and {@code MS SQL Server 2019}
     */
    public CloseableIterator<PROJECTED> iterateAll(QueryModifier<ROOT, PROJECTED> modifier) {
        JPAQuery<PROJECTED> query = query()
                .select(metaModel)
                .from(metaModel);

        modifier.modify(query);

        // define ordering to not retrieve records in random order
        query.orderBy(defaultListAllOrder());

        ScrollableResults results = query.createQuery()
                .unwrap(org.hibernate.query.Query.class)
                .scroll(ScrollMode.FORWARD_ONLY);

        return new ScrollableResultsIterator<>(results);
    }

    /**
     * Returns all instances.
     *
     * @param listFunction List function to use.
     */
    public List<PROJECTED> listAll(ListFunction<ROOT, PROJECTED> listFunction) {
        return listFunction.call((query) -> {});
    }

    /**
     * Finds the objects corresponding to the specified list of IDs using {@link DomainStore#listDefault(QueryModifier)}.
     *
     * @param ids list of IDs
     * @return collection of found objects
     */
    public List<PROJECTED> listByIds(@NotNull List<String> ids) {
        return listByIds(ids, this::listDefault);
    }

    /**
     * Returns objects in batches using {@link DomainStore#listDefault(QueryModifier)}.
     *
     * @param offset defines the offset for the query results
     * @param limit  defines the limit / max results for the query results
     * @return collection of found objects
     */
    public List<PROJECTED> listByWindow(long offset, long limit) {
        return listByWindow(offset, limit, this::listDefault);
    }

    /**
     * Returns all objects using {@link DomainStore#listDefault(QueryModifier)}.
     *
     * @return collection of found objects
     */
    public List<PROJECTED> listAll() {
        return listAll(this::listDefault);
    }

    /**
     * Deletes object with given id.
     *
     * @param id id of instance to delete
     * @return resultant object or {@code null} if the object was not found
     */
    public PROJECTED delete(@NotNull String id) {
        PROJECTED entity = findConnected(id);

        if (entity != null) {
            entityManager.remove(entity);
            entityManager.flush();
        }
        return entity;
    }

    /**
     * Deletes given collection of objects in a batch.
     *
     * @param ids collection of ids of objects to delete
     * @return collection of deleted objects
     */
    public Collection<PROJECTED> delete(@NotNull Collection<String> ids) {
        if (ids.isEmpty()) {
            return emptyList();
        }

        List<PROJECTED> deletedEntities = ids.stream()
                .filter(Objects::nonNull)
                .map(this::findConnected)
                .filter(Objects::nonNull)
                .peek(entityManager::remove)
                .collect(Collectors.toList());

        entityManager.flush();
        detachAll();

        return deletedEntities;
    }

    /**
     * Returns dummy object with specified id.
     */
    @SneakyThrows
    public PROJECTED getRef(String id) {
        Constructor<? extends PROJECTED> constructor = type.getDeclaredConstructor();
        PROJECTED instance = constructor.newInstance();
        instance.setId(id);

        return instance;
    }

    /**
     * Returns the single object with given ID still connected to JPA context.
     *
     * For internal use only.
     *
     * @param id ID of instance to be returned
     * @return found instance or {@code null} if not found
     */
    public PROJECTED findConnected(@NotNull String id) {
        JPAQuery<PROJECTED> query = query().
                select(metaModel).
                from(metaModel)
                .where(domainMetaModel.id.eq(id));

        return query.fetchFirst();
    }

    /**
     * Sorts by id.
     */
    protected OrderSpecifier<?>[] defaultListAllOrder() {
        return new OrderSpecifier[]{domainMetaModel.id.asc()};
    }

    @SuppressWarnings("unchecked")
    private META_MODEL deriveMetaModel(Class<? extends PROJECTED> type) {
        String simpleName = type.getSimpleName();
        String qTypeName = type.getPackageName() + ".Q" + simpleName;

        try {
            Class<META_MODEL> qType = (Class<META_MODEL>) Class.forName(qTypeName);
            Constructor<META_MODEL> constructor = qType.getConstructor(String.class);
            return constructor.newInstance(Introspector.decapitalize(simpleName));
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | ClassNotFoundException e) {
            throw new GeneralException("Error creating Q object for " + simpleName, e);
        }
    }

    /**
     * Resolve {@link ROOT} from given {@link PROJECTED} type.
     *
     * {@link PROJECTED} type must implement {@link Domain<ROOT>} because of its declaration.
     *
     * {@link ROOT} can be resolved from the interface generics.
     */
    @SuppressWarnings("unchecked")
    private Class<ROOT> deriveRootType(Class<? extends PROJECTED> type) {
        for (Class<?> declaredInterface : ClassUtils.getAllInterfacesForClassAsSet(type)) {
            if (declaredInterface.equals(Domain.class)) {
                // when class implementing Domain is found, use its declaration Domain<ROOT> for resolving the ROOT
                return (Class<ROOT>) TypeResolver.resolveRawArgument(declaredInterface, type);
            }
        }

        // should never happen as PROJECTED is declared to extend Domain<ROOT> in class declaration,
        // therefore it must implement Domain interface
        throw new IllegalArgumentException("Provided type " + type.getName() + " does not implement " + Domain.class.getName());
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired(required = false)
    public void setDifferModuleAndProcessFields(DifferModule differModule) {
        this.differModule = differModule;

        if (differModule == null) {
            // DifferModule not beaned -> not enabled -> skip
            return;
        }

        this.differModule.processClass(type);
    }
}
