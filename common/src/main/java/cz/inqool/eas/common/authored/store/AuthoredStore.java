package cz.inqool.eas.common.authored.store;

import com.querydsl.core.types.dsl.EntityPathBase;
import cz.inqool.eas.common.authored.Authored;
import cz.inqool.eas.common.authored.tenant.TenantGenerator;
import cz.inqool.eas.common.authored.tenant.TenantReference;
import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.dated.store.DatedStore;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Database store for objects extending {@link AuthoredObject} with standard CRUD operations.
 *
 * Entities are marked with deletedBy user.
 *
 * @param <ROOT> Entity type
 * @param <PROJECTED> Entity projection type
 * @param <META_MODEL> Meta model for projection type
 */
public class AuthoredStore<ROOT extends Authored<ROOT>, PROJECTED extends Authored<ROOT>, META_MODEL extends EntityPathBase<PROJECTED>> extends DatedStore<ROOT, PROJECTED, META_MODEL> {
    protected final QAuthoredObject authoredMetaModel;

    public AuthoredStore(Class<? extends PROJECTED> type) {
        super(type);

        this.authoredMetaModel = new QAuthoredObject(this.metaModel.getMetadata());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PROJECTED create(@NotNull PROJECTED entity) {
        entity.setDeletedBy(null);
        entity.setDeletedByTenant(null);
        return super.create(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends PROJECTED> create(@NotNull Collection<? extends PROJECTED> entities) {
        entities.forEach(entity -> {
            entity.setDeletedBy(null);
            entity.setDeletedByTenant(null);
        });
        return super.create(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PROJECTED update(@NotNull PROJECTED entity) {
        entity.setUpdatedBy(null); // to force hibernate to auto-generate new value
        entity.setUpdatedByTenant(null); // to force hibernate to auto-generate new value
        entity.setDeletedBy(null);
        entity.setDeletedByTenant(null);
        return super.update(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends PROJECTED> update(@NotNull Collection<? extends PROJECTED> entities) {
        entities.forEach(entity -> {
            entity.setUpdatedBy(null); // to force hibernate to auto-generate new value
            entity.setUpdatedByTenant(null); // to force hibernate to auto-generate new value
            entity.setDeletedBy(null);
            entity.setDeletedByTenant(null);
        });
        return super.update(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDeletedFlag(PROJECTED projected) {
        super.setDeletedFlag(projected);

        projected.setDeletedBy(UserGenerator.generateValue());
        projected.setDeletedByTenant(TenantGenerator.generateValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDeletedFlag(Collection<? extends PROJECTED> projected) {
        super.setDeletedFlag(projected);

        UserReference user = UserGenerator.generateValue();
        TenantReference tenant = TenantGenerator.generateValue();
        projected.forEach(o -> {
            o.setDeletedBy(user);
            o.setDeletedByTenant(tenant);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setRestoredFlag(PROJECTED projected) {
        super.setRestoredFlag(projected);

        projected.setDeletedBy(null);
        projected.setDeletedByTenant(null);
    }

    /**
     * Gets {@link UserReference} who created the object specified by id.
     *
     * @param id Id of object
     * @return UserReference or null if object does not exist.
     */
    public UserReference getAuthor(String id) {
        UserReference user = query().
                select(authoredMetaModel.createdBy).
                from(metaModel).
                where(authoredMetaModel.id.eq(id)).
                fetchFirst();

        detachAll();

        return user;
    }

    /**
     * Gets {@link TenantReference} who created the object specified by id.
     *
     * @param id Id of object
     * @return TenantReference or null if object does not exist or tenant is not set
     */
    public TenantReference getAuthorTenant(String id) {
        TenantReference tenant = query().
                select(authoredMetaModel.createdByTenant).
                from(metaModel).
                where(authoredMetaModel.id.eq(id)).
                fetchFirst();

        detachAll();

        return tenant;
    }
}
