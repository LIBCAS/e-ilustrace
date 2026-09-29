package cz.inqool.eas.common.dictionary;

import cz.inqool.eas.common.authored.AuthoredRepository;
import cz.inqool.eas.common.dictionary.index.DictionaryAutocomplete;
import cz.inqool.eas.common.dictionary.index.DictionaryIndex;
import cz.inqool.eas.common.dictionary.index.DictionaryIndexedObject;
import cz.inqool.eas.common.dictionary.index.DictionaryIndexedObject.Fields;
import cz.inqool.eas.common.dictionary.store.DictionaryStore;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.filter.*;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.domain.index.dto.sort.FieldSort;
import cz.inqool.eas.common.intl.Language;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.sort.SortOrder;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

/**
 * Repository for objects implementing {@link Dictionary} interface.
 *
 * Database and index functionality combined together.
 *
 * @param <ROOT>            Root of the projection type system
 * @param <INDEX_PROJECTED> Index projection type
 * @param <INDEXED>         Indexed object type
 * @param <STORE>           Type of store
 * @param <INDEX>           Type of index
 */
@Slf4j
public class DictionaryRepository<
        ROOT extends Dictionary<ROOT>,
        INDEX_PROJECTED extends Dictionary<ROOT>,
        INDEXED extends DictionaryIndexed<ROOT, INDEX_PROJECTED>,
        STORE extends DictionaryStore<ROOT, ROOT, ?>,
        INDEX extends DictionaryIndex<ROOT, INDEX_PROJECTED, INDEXED>
        > extends AuthoredRepository<
                ROOT,
                INDEX_PROJECTED,
                INDEXED,
                STORE,
                INDEX> {

    /**
     * Activates object.
     *
     * @param id Id of object to activate
     * @return Object that was activated in both DB and Index.
     */
    public ROOT activate(String id) {
        ROOT root = getStore().activate(id);
        getIndex().activate(id);

        return root;
    }

    /**
     * @see DictionaryStore#findByCode(String)
     */
    public <PROJECTED extends Dictionary<ROOT>> PROJECTED findByCode(Class<PROJECTED> type, @NotNull String code) {
        return getDictionaryStore(type).findByCode(code);
    }

    /**
     * @see DictionaryStore#findByName(String) (String)
     */
    public <PROJECTED extends Dictionary<ROOT>> PROJECTED findByName(Class<PROJECTED> type, @NotNull String name) {
        return getDictionaryStore(type).findByName(name);
    }

    /**
     * Deactivates object.
     *
     * @param id Id of object to deactivate
     * @return Object that was deactivated in both DB and Index.
     */
    public ROOT deactivate(String id) {
        ROOT root = getStore().deactivate(id);
        getIndex().deactivate(id);

        return root;
    }

    /**
     * Retrieves objects using provided projection type and query string.
     * <p>
     * Used for autocomplete endpoints.
     *
     * @param query  Query string
     * @param params Default params
     */
    public Result<DictionaryAutocomplete> listAutocomplete(@Nullable String query, @Nullable Language language, @Nullable Params params, boolean all) {
        params = prepareAutocompleteParams(params, language, query, all);

        return this.getIndex().listByParams(params, (builder) -> builder.fetchSource(new String[]{Fields.name, Fields.multiName, Fields.code}, null), (hit) -> {
            String value = (String) hit.getSourceAsMap().get(Fields.name);
            if (language != null) {
                value = ((HashMap<Language, String>) hit.getSourceAsMap().get(Fields.multiName)).get(language.name().toLowerCase());
            }
            String code = (String) hit.getSourceAsMap().get(Fields.code);

            return new DictionaryAutocomplete(hit.getId(), value, code);
        });
    }

    /**
     * Retrieves all objects using provided projection type and query string.
     * <p>
     * Used for autocomplete endpoints.
     *
     * @param query  Query string
     * @param params Default params
     */
    public List<DictionaryAutocomplete> listAutocompleteFull(@Nullable String query, @Nullable Language language, @Nullable Params params, boolean all) {
        params = prepareAutocompleteParams(params, language, query, all);

        return this.getIndex().listAllByParams(params, (builder) -> builder.fetchSource(new String[]{Fields.name, Fields.multiName, Fields.code}, null), (hit) -> {
            String value = (String) hit.getSourceAsMap().get(Fields.name);
            if (language != null) {
                value = ((HashMap<Language, String>) hit.getSourceAsMap().get(Fields.multiName)).get(language.name().toLowerCase());
            }
            String code = (String) hit.getSourceAsMap().get(Fields.code);

            return new DictionaryAutocomplete(hit.getId(), value, code);
        });
    }

    protected Params prepareAutocompleteParams(Params params, @Nullable Language language, @Nullable String query, boolean all) {
        if (params == null) {
            params = new Params();
        }

        params.setSort(List.of(new FieldSort(Fields.order, SortOrder.ASC), new FieldSort(DictionaryIndexedObject.Fields.name, SortOrder.ASC)));

        // text filter
        if (query != null) {
            if (language == null) {
                params.addFilter(new ContainsFilter(Fields.name, query));
            } else {
                params.addFilter(new ContainsFilter(Fields.multiName + "." + language.name().toLowerCase(), query));
            }
        }

        if (!all) {
            // only active
            params.addFilter(new EqFilter(Fields.active, "true"));

            // only valid
            String now = Instant.now().toString();

            params.addFilter(new OrFilter(new NullFilter(Fields.validFrom), new LteFilter(Fields.validFrom, now)));
            params.addFilter(new OrFilter(new NullFilter(Fields.validTo), new GteFilter(Fields.validTo, now)));
        }

        return params;
    }

    @SuppressWarnings("unchecked")
    protected <PROJECTED extends Dictionary<ROOT>> DictionaryStore<ROOT, PROJECTED, ?> getDictionaryStore(Class<PROJECTED> type) {
        return (DictionaryStore<ROOT, PROJECTED, ?>) super.getStore(type);
    }
}
