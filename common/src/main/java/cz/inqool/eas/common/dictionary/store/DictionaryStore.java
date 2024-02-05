package cz.inqool.eas.common.dictionary.store;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.common.dictionary.Dictionary;
import cz.inqool.eas.common.exception.v2.MissingObject;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Collection;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

/**
 * Database store for objects extending {@link DictionaryObject} with standard CRUD operations.
 *
 * @param <ROOT>       Entity type
 * @param <PROJECTED>  Entity projection type
 * @param <META_MODEL> Meta model for projection type
 */
public class DictionaryStore<
        ROOT extends Dictionary<ROOT>,
        PROJECTED extends Dictionary<ROOT>,
        META_MODEL extends EntityPathBase<PROJECTED>
        > extends AuthoredStore<
        ROOT,
        PROJECTED,
        META_MODEL
        > {
    protected final QDictionaryObject dictionaryMetaModel;

    public DictionaryStore(Class<PROJECTED> type) {
        super(type);

        this.dictionaryMetaModel = new QDictionaryObject(this.metaModel.getMetadata());
    }

    /**
     * Activates object.
     *
     * @param id Id of object to activate
     * @return Object that was activated in DB.
     */
    public PROJECTED activate(String id) {
        PROJECTED projected = this.find(id);
        notNull(projected, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", this.getType().getSimpleName()))
                .debugInfo(info -> info.clazz(this.getType()))
                .logAll());

        projected.setActive(true);
        return this.update(projected);
    }

    /**
     * Deactivates object.
     *
     * @param id Id of object to disable
     * @return Object that was deactivated in DB.
     */
    public PROJECTED deactivate(String id) {
        PROJECTED projected = this.find(id);
        notNull(projected, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", this.getType().getSimpleName()))
                .debugInfo(info -> info.clazz(this.getType()))
                .logAll());

        projected.setActive(false);
        return this.update(projected);
    }

    /**
     * Finds dictionary object based on code.
     *
     * @param code Code of object
     * @return Found object or null
     */
    public PROJECTED findByCode(@NotNull String code) {
        Instant now = Instant.now();

        JPAQuery<PROJECTED> query = query().
                select(metaModel).
                from(metaModel)
                .where(dictionaryMetaModel.code.eq(code))
                .where(dictionaryMetaModel.deleted.isNull())
                .where(dictionaryMetaModel.active.isTrue())
                .where(dictionaryMetaModel.validFrom.isNull().or(dictionaryMetaModel.validFrom.loe(now)))
                .where(dictionaryMetaModel.validTo.isNull().or(dictionaryMetaModel.validTo.gt(now)));

        PROJECTED projected = query.fetchFirst();

        detachAll();

        return projected;
    }

    /**
     * Finds dictionary object based on exact match of name.
     *
     * @param name Name of the object
     * @return Found object or null
     */
    public PROJECTED findByName(@NotNull String name) {
        Instant now = Instant.now();

        JPAQuery<PROJECTED> query = query().
                select(metaModel).
                from(metaModel)
                .where(dictionaryMetaModel.name.eq(name))
                .where(dictionaryMetaModel.deleted.isNull())
                .where(dictionaryMetaModel.active.isTrue())
                .where(dictionaryMetaModel.validFrom.isNull().or(dictionaryMetaModel.validFrom.loe(now)))
                .where(dictionaryMetaModel.validTo.isNull().or(dictionaryMetaModel.validTo.gt(now)));

        PROJECTED projected = query.fetchFirst();

        detachAll();

        return projected;
    }

    /**
     * Returns all active instances.
     *
     * @return collection of found instances
     */
    public Collection<PROJECTED> listActive() {
        return listDefault((query) -> query.where(dictionaryMetaModel.active.eq(true)));
    }
}
