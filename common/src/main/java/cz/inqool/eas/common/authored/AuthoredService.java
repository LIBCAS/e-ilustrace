package cz.inqool.eas.common.authored;

import cz.inqool.eas.common.authored.tenant.TenantGenerator;
import cz.inqool.eas.common.authored.tenant.TenantReference;
import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.projection.Projectable;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.authored.AuthoredUtils.tenantFilter;
import static cz.inqool.eas.common.authored.AuthoredUtils.userFilter;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.OBJECT_ACCESS_DENIED;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.OPERATION_ACCESS_DENIED;
import static cz.inqool.eas.common.utils.AssertionUtils.*;

/**
 * CRUD Service layer for objects implementing {@link Authored}.
 *
 * @param <ROOT>              Root of the projection type system
 * @param <DETAIL_PROJECTION> Projection used for retrieving object detail
 * @param <LIST_PROJECTION>   Projection used for listing object
 * @param <CREATE_PROJECTION> Projection used as DTO for object creation
 * @param <UPDATE_PROJECTION> Projection used as DTO for object update
 * @param <REPOSITORY>        Repository type
 */
public abstract class AuthoredService<
        ROOT extends Authored<ROOT>,
        DETAIL_PROJECTION extends Authored<ROOT>,
        LIST_PROJECTION extends Authored<ROOT>,
        CREATE_PROJECTION extends Projectable<ROOT>,
        UPDATE_PROJECTION extends Projectable<ROOT>,
        REPOSITORY extends AuthoredRepository<ROOT, ?, ?, ?, ?>
        >
        extends DatedService<
        ROOT,
        DETAIL_PROJECTION,
        LIST_PROJECTION,
        CREATE_PROJECTION,
        UPDATE_PROJECTION,
        REPOSITORY> {

    /**
     * Retrieves list view of objects that respect the selected {@link Params} and was created by current user.
     *
     * @param params Parameters to comply with
     * @return Sorted list of objects with total number
     */
    @Transactional
    public Result<LIST_PROJECTION> listMine(@Nullable Params params) {
        Access access = readAccess();

        if (access != Access.NONE) {
            params = coalesce(params, Params::new);
            params.addFilter(userFilter());
            return super.list(params);
        } else {
            throw new ForbiddenOperation(OPERATION_ACCESS_DENIED)
                    .details(details -> details.property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType));
        }
    }

    /**
     * Retrieves list view of objects that respect the selected {@link Params} and were created by current user tenant.
     *
     * @param params Parameters to comply with
     * @return Sorted list of objects with total number
     */
    @Transactional
    public Result<LIST_PROJECTION> listOur(@Nullable Params params) {
        Access access = readAccess();

        if (access != Access.NONE && access != Access.SELF) {
            params = coalesce(params, Params::new);
            params.addFilter(tenantFilter());
            return super.list(params);
        } else {
            throw new ForbiddenOperation(OPERATION_ACCESS_DENIED)
                    .details(details -> details.property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType));
        }
    }

    @Transactional
    public UserReference getAuthor(String id) {
        return repository.getAuthor(id);
    }

    @Transactional
    public TenantReference getAuthorTenant(String id) {
        return repository.getAuthorTenant(id);
    }

    /**
     * {@inheritDoc}
     *
     * Adds checking for write access.
     */
    @Override
    protected void preCreateHook(@NotNull ROOT object) {
        Access access = writeAccess();

        ne(access, Access.NONE, () -> new ForbiddenOperation(OPERATION_ACCESS_DENIED)
                .details(details -> details.property("type", rootType.getSimpleName()))
                .debugInfo(info -> info.clazz(rootType)));
    }

    /**
     * {@inheritDoc}
     *
     * Adds checking for write access.
     */
    @Override
    protected void preUpdateHook(@NotNull ROOT object) {
        String id = object.getId();

        Access access = writeAccess();

        if (access == Access.TENANT) {
            TenantReference tenant = TenantGenerator.generateValue();
            TenantReference author = getAuthorTenant(id);
            eq(tenant, author, () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType).property("tenant", tenant).property("author", author)));

        } else if (access == Access.SELF) {
            UserReference user = UserGenerator.generateValue();
            UserReference author = getAuthor(id);
            eq(user, author, () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType).property("user", user).property("author", author)));

        } else if (access == Access.NONE) {
            throw new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType));
        }
    }

    /**
     * {@inheritDoc}
     *
     * Adds checking for write access.
     */
    @Override
    protected void preDeleteHook(@NotNull String id) {
        Access access = writeAccess();

        if (access == Access.TENANT) {
            TenantReference tenant = TenantGenerator.generateValue();
            TenantReference author = getAuthorTenant(id);
            eq(tenant, author, () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType).property("tenant", tenant).property("author", author)));

        } else if (access == Access.SELF) {
            UserReference user = UserGenerator.generateValue();
            UserReference author = getAuthor(id);
            eq(user, author, () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType).property("user", user).property("author", author)));

        } else if (access == Access.NONE) {
            throw new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType));
        }
    }

    /**
     * {@inheritDoc}
     *
     * Adds checking for read access.
     */
    @Override
    protected void preGetHook(@NotNull String id) {
        Access access = readAccess();

        if (access == Access.TENANT) {
            TenantReference tenant = TenantGenerator.generateValue();
            TenantReference author = getAuthorTenant(id);
            eq(tenant, author, () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType).property("tenant", tenant).property("author", author)));

        } else if (access == Access.SELF) {
            UserReference user = UserGenerator.generateValue();
            UserReference author = getAuthor(id);
            eq(user, author, () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType).property("user", user).property("author", author)));

        } else if (access == Access.NONE) {
            throw new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType));
        }
    }

    /**
     * {@inheritDoc}
     *
     * Based on access add none or own (user or tenant) filter.
     */
    @Override
    protected void preListHook(@NotNull Params params) {
        Access access = readAccess();

        if (access == Access.TENANT) {
            params.addFilter(tenantFilter());
        } else if (access == Access.SELF) {
            params.addFilter(userFilter());
        } else if (access == Access.NONE) {
            throw new ForbiddenOperation(OPERATION_ACCESS_DENIED)
                    .details(details -> details.property("type", rootType.getSimpleName()))
                    .debugInfo(info -> info.clazz(rootType));
        }
    }

    /**
     * Specifies types of read access based on user and tenant. Does not specify permissions.
     *
     * Override in subclasses to change the access.
     *
     * @return access
     */
    protected Access readAccess() {
        return Access.ALL;
    }

    /**
     * Specifies types of write access based on user and tenant. Does not specify permissions.
     *
     * Override in subclasses to change the access.
     *
     * @return access
     */
    protected Access writeAccess() {
        return Access.ALL;
    }
}
