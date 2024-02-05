package cz.inqool.eas.common.dated.store;

import com.google.common.collect.Iterables;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import cz.inqool.eas.common.dated.Dated;
import cz.inqool.eas.common.domain.store.DomainStore;
import cz.inqool.eas.common.domain.store.list.QueryModifier;
import cz.inqool.eas.common.exception.v2.PersistenceException;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.DELETED_ENTITY_NOT_UPDATABLE;
import static java.util.Collections.emptyList;

/**
 * Database store for objects extending {@link DatedObject} with standard CRUD operations.
 *
 * Entities are not deleted from database, rather a deleted flag is set.
 *
 * @param <ROOT>       Entity type
 * @param <PROJECTED>  Entity projection type
 * @param <META_MODEL> Meta model for projection type
 */
public class DatedStore<
        ROOT extends Dated<ROOT>,
        PROJECTED extends Dated<ROOT>,
        META_MODEL extends EntityPathBase<PROJECTED>>
        extends DomainStore<
        ROOT,
        PROJECTED,
        META_MODEL
        > {
    protected final QDatedObject datedMetaModel;

    public DatedStore(Class<? extends PROJECTED> type) {
        super(type);

        this.datedMetaModel = new QDatedObject(this.metaModel.getMetadata());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PROJECTED create(@NotNull PROJECTED entity) {
        entity.setDeleted(null);
        return super.create(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends PROJECTED> create(@NotNull Collection<? extends PROJECTED> entities) {
        entities.forEach(entity -> entity.setDeleted(null));
        return super.create(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PROJECTED update(@NotNull PROJECTED entity) {
        if (isDeleted(entity.getId())) {
            throw new PersistenceException(DELETED_ENTITY_NOT_UPDATABLE, "Deleted entity can not be updated.")
                    .details(details -> details.property("type", getType().getSimpleName()).property("id", entity.getId()))
                    .debugInfo(info -> info.property("type", getType()))
                    .logAll();
        }
        entity.setUpdated(null); // to force hibernate to auto-generate new value
        entity.setDeleted(null);
        return super.update(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends PROJECTED> update(@NotNull Collection<? extends PROJECTED> entities) {
        List<String> entityIDs = entities.stream()
                .map(Dated::getId)
                .collect(Collectors.toList());

        if (isAnyDeleted(entityIDs)) {
            throw new PersistenceException(DELETED_ENTITY_NOT_UPDATABLE, "Deleted entities can not be updated.")
                    .details(details -> details.property("type", getType()).property("ids", entityIDs))
                    .debugInfo(info -> info.property("type", getType()))
                    .logAll();
        }
        entities.forEach(entity -> {
            entity.setUpdated(null); // to force hibernate to auto-generate new value
            entity.setDeleted(null);
        });
        return super.update(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PROJECTED delete(@NotNull String id) {
        PROJECTED entity = findConnected(id);

        if (entity != null) {
            setDeletedFlag(entity);

            entityManager.flush();
            detachAll();
        }

        return entity;
    }

    /**
     * Deletes an instance permanently (instead of keeping it with the deleted flag set). Non existing instance is
     * silently skipped.
     *
     * @param id ID of instance to delete
     * @return resultant instance (with uninitialized lazy collections) or {@code null} if the entity was not found
     */
    public PROJECTED deletePermanently(@NotNull String id) {
        return super.delete(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<PROJECTED> delete(@NotNull Collection<String> ids) {
        if (ids.isEmpty()) {
            return emptyList();
        }

        List<PROJECTED> deletedEntities = ids.stream()
                .filter(Objects::nonNull)
                .map(this::findConnected)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        setDeletedFlag(deletedEntities);

        entityManager.flush();
        detachAll();

        return deletedEntities;
    }

    /**
     * Deletes instances permanently (instead of keeping them with the deleted flag set). Non existing instances are
     * silently skipped.
     *
     * @see #deletePermanently(String)
     */
    public Collection<PROJECTED> deletePermanently(@NotNull Collection<String> ids) {
        return super.delete(ids);
    }

    /**
     * {@inheritDoc}
     *
     * Filter out deleted entities.
     */
    @Override
    public List<PROJECTED> listDefault(QueryModifier<ROOT, PROJECTED> queryModifier) {
        return super.list((query) -> {
            query.where(datedMetaModel.deleted.isNull());

            queryModifier.modify(query);
        });
    }

    /**
     * Tests if entity with specified id is among deleted.
     *
     * @param id Id of entity
     * @return deleted status
     */
    public boolean isDeleted(@NotNull String id) {
        return query().
                select(metaModel).
                from(metaModel).
                where(datedMetaModel.id.eq(id)).
                where(datedMetaModel.deleted.isNotNull()).
                fetchCount() > 0;
    }

    /**
     * Tests if any entity from collection of ids is among deleted.
     *
     * @param ids Collection of ids
     * @return deleted status
     */
    public boolean isAnyDeleted(@NotNull Collection<String> ids) {
        int maxParameterCountLimit = 1000; // DBMS have limits for parameter count, 1K is used as a practical value
        Iterable<List<String>> idsBatches = Iterables.partition(ids, maxParameterCountLimit);

        return StreamSupport.stream(idsBatches.spliterator(), false)
                .anyMatch(idsBatch -> query().
                        select(metaModel).
                        from(metaModel).
                        where(datedMetaModel.id.in(idsBatch)).
                        where(datedMetaModel.deleted.isNotNull()).
                        fetchCount() > 0);
    }

    /**
     * Restores deleted entity.
     *
     * @param id Id of deleted entity to restore
     * @return restored entity
     */
    public PROJECTED restore(@NotNull String id) {
        PROJECTED entity = findConnected(id);

        if (entity != null) {
            setRestoredFlag(entity);

            entityManager.flush();
            detachAll();
        }
        return entity;
    }

    /**
     * Sorts by created and id.
     */
    @Override
    protected OrderSpecifier<?>[] defaultListAllOrder() {
        return new OrderSpecifier[]{
                datedMetaModel.created.asc(),
                datedMetaModel.id.asc()
        };
    }

    /**
     * Updates entity with deleted flag.
     *
     * @param projected entity
     */
    protected void setDeletedFlag(PROJECTED projected) {
        projected.setDeleted(InstantGenerator.generateValue());
    }

    /**
     * Updates entity with deleted flag.
     *
     * @param projected entity
     */
    protected void setDeletedFlag(Collection<? extends PROJECTED> projected) {
        final Instant now = InstantGenerator.generateValue();

        projected.forEach(e -> e.setDeleted(now));
    }

    /**
     * Updates entity with restored flag.
     *
     * @param projected entity
     */
    protected void setRestoredFlag(PROJECTED projected) {
        projected.setDeleted(null);
    }
}
