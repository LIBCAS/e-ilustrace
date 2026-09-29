package cz.inqool.eas.common.authored;

import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.common.authored.tenant.TenantReference;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.dated.DatedRepository;

/**
 * Repository for objects implementing {@link Authored} interface.
 *
 * Database and index functionality combined together.
 *
 * @param <ROOT>            Root of the projection type system
 * @param <INDEX_PROJECTED> Index projection type
 * @param <INDEXED>         Indexed object type
 * @param <STORE>           Type of store
 * @param <INDEX>           Type of index
 */
public class AuthoredRepository<
        ROOT extends Authored<ROOT>,
        INDEX_PROJECTED extends Authored<ROOT>,
        INDEXED extends AuthoredIndexed<ROOT, INDEX_PROJECTED>,
        STORE extends AuthoredStore<ROOT, ROOT, ?>,
        INDEX extends AuthoredIndex<ROOT, INDEX_PROJECTED, INDEXED>
        > extends DatedRepository<
                ROOT,
                INDEX_PROJECTED,
                INDEXED,
                STORE,
                INDEX
                > {

    public UserReference getAuthor(String id) {
        return getStore().getAuthor(id);
    }

    public TenantReference getAuthorTenant(String id) {
        return getStore().getAuthorTenant(id);
    }
}
