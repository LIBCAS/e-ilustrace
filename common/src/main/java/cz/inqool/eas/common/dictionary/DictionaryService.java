package cz.inqool.eas.common.dictionary;

import cz.inqool.eas.common.authored.AuthoredService;
import cz.inqool.eas.common.dictionary.event.ActivateEvent;
import cz.inqool.eas.common.dictionary.event.DeactivateEvent;
import cz.inqool.eas.common.dictionary.index.DictionaryAutocomplete;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.intl.Language;
import cz.inqool.eas.common.projection.Projectable;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

/**
 * CRUD Service layer for objects implementing {@link Dictionary}.
 *
 * @param <ROOT>              Root of the projection type system
 * @param <DETAIL_PROJECTION> Projection used for retrieving object detail
 * @param <LIST_PROJECTION>   Projection used for listing object
 * @param <CREATE_PROJECTION> Projection used as DTO for object creation
 * @param <UPDATE_PROJECTION> Projection used as DTO for object update
 * @param <REPOSITORY>        Repository type
 */
public abstract class DictionaryService<
        ROOT extends Dictionary<ROOT>,
        DETAIL_PROJECTION extends Dictionary<ROOT>,
        LIST_PROJECTION extends Dictionary<ROOT>,
        CREATE_PROJECTION extends Projectable<ROOT>,
        UPDATE_PROJECTION extends Projectable<ROOT>,
        REPOSITORY extends DictionaryRepository<ROOT, ?, ?, ?, ?>
        >
        extends AuthoredService<
        ROOT,
        DETAIL_PROJECTION,
        LIST_PROJECTION,
        CREATE_PROJECTION,
        UPDATE_PROJECTION,
        REPOSITORY> {

    /**
     * Gets detail projection of object based on code.
     *
     * @param code Code of the object
     * @return Detail view of the object
     */
    @Transactional
    public DETAIL_PROJECTION getByCode(String code) {
        return this.getInternalByCode(detailType, code);
    }

    /**
     * Gets object based on code using projection.
     *
     * preGetHook is called after retrieval, because otherwise we don't know the id of the object.
     *
     * @param type        Projection type
     * @param code        Code of the object
     * @param <PROJECTED> Type of projection
     * @return The object
     */
    @Transactional
    public <PROJECTED extends Dictionary<ROOT>> PROJECTED getInternalByCode(Class<PROJECTED> type, @NotNull String code) {

        PROJECTED view = repository.findByCode(type, code);
        notNull(view, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.property("type", rootType.getSimpleName()).property("code", code))
                .debugInfo(info -> info.clazz(rootType))
                .logAll());

        preGetHook(view.getId());
        doWithRoot(view, this::postGetHook);

        return view;
    }

    public Result<DictionaryAutocomplete> listAutocomplete(@Nullable String query, @Nullable Language language, @Nullable Params params) {
        preAutocompleteHook(params);
        return this.repository.listAutocomplete(query, language, params, false);
    }

    public Result<DictionaryAutocomplete> listAutocompleteAll(@Nullable String query, @Nullable Language language, @Nullable Params params) {
        preAutocompleteHook(params);
        return this.repository.listAutocomplete(query, language, params, true);
    }

    public List<DictionaryAutocomplete> listAutocompleteFull(@Nullable String query, @Nullable Language language, @Nullable Params params) {
        preAutocompleteHook(params);
        return this.repository.listAutocompleteFull(query, language, params, false);
    }

    public List<DictionaryAutocomplete> listAutocompleteFullAll(@Nullable String query, @Nullable Language language, @Nullable Params params) {
        preAutocompleteHook(params);
        return this.repository.listAutocompleteFull(query, language, params, true);
    }

    /**
     * Activates object.
     *
     * PostUpdate hook is called, but not preUpdate.
     *
     * @param id Id of object to activate
     */
    @Transactional
    public void activate(String id) {
        preStatusChangeHook(id);
        ROOT root = repository.activate(id);
        postUpdateHook(root);

        eventPublisher.publishEvent(new ActivateEvent<>(this, root));
    }

    /**
     * Deactivates object.
     *
     * PostUpdate hook is called, but not preUpdate.
     *
     * @param id Id of object to deactivate
     */
    @Transactional
    public void deactivate(String id) {
        preStatusChangeHook(id);
        ROOT root = repository.deactivate(id);
        postUpdateHook(root);

        eventPublisher.publishEvent(new DeactivateEvent<>(this, root));
    }

    /**
     * Hook called before listing objects for autocomplete.
     */
    protected void preAutocompleteHook(@NotNull Params params) {
    }

    /**
     * Hook called before activating or deactivating object.
     */
    protected void preStatusChangeHook(@NotNull String id) {
    }
}
