package cz.inqool.eas.common.dictionary.index;

import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.dictionary.DictionaryIndexed;
import cz.inqool.eas.common.projection.Projectable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;

/**
 * Index store for objects extending {@link DictionaryIndexedObject} with standard CRUD operations.
 *
 * @param <ROOT>      Root of the projection type system
 * @param <PROJECTED> Index projection type
 * @param <INDEXED>   Indexed object type
 */
@Slf4j
@Setter
public class DictionaryIndex<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>, INDEXED extends DictionaryIndexed<ROOT, PROJECTED>> extends AuthoredIndex<ROOT, PROJECTED, INDEXED> {
    public DictionaryIndex(Class<INDEXED> indexedType) {
        super(indexedType);
    }

    public void activate(String id) {
        UpdateRequest request = createActivateRequest(id, true);
        execute(client -> client.update(request, RequestOptions.DEFAULT));
        refresh();
    }

    public void deactivate(String id) {
        UpdateRequest request = createActivateRequest(id, false);
        execute(client -> client.update(request, RequestOptions.DEFAULT));
        refresh();
    }

    private UpdateRequest createActivateRequest(String id, boolean active) {
        return new UpdateRequest()
                .index(getIndexName())
                .id(id)
                .doc("active", active);
    }
}
