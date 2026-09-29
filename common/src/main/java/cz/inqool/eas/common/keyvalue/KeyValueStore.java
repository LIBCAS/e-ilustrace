package cz.inqool.eas.common.keyvalue;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stores simple pairs of string-string into database.
 * Usable for storing various configs that need to be changed from frontend without restart, simple stats,
 * or last time or object id of some integration.
 *
 * @author Lukas Jane (inQool) 23.04.2018.
 */
public class KeyValueStore extends DomainStore<KeyValue, KeyValue, QKeyValue> {
    public KeyValueStore() {
        super(KeyValue.class);
    }

    public String getValue(KeyValue.Key key) {
        return query()
                .from(metaModel)
                .select(metaModel.value)
                .where(metaModel.key.eq(key.name()))
                .fetchOne();
    }

    @Transactional
    public void setValue(KeyValue.Key key, String value) {
        KeyValue keyValue = get(key);
        if (keyValue == null) {
            keyValue = new KeyValue(key.name(), value);
            create(keyValue);
        }
        else {
            keyValue.setValue(value);
            update(keyValue);
        }
    }

    /**
     * For setting defaults
     */
    @Transactional
    public void setIfNull(KeyValue.Key key, String value) {
        if (get(key) == null) {
            setValue(key, value);
        }
    }

    @Transactional
    public void deleteValue(KeyValue.Key key) {
        KeyValue keyValue = get(key);
        if (keyValue != null) {
            delete(keyValue.getId());
        }
    }

    private KeyValue get(KeyValue.Key key) {
        KeyValue keyValue = query()
                .from(metaModel)
                .select(metaModel)
                .where(metaModel.key.eq(key.name()))
                .fetchOne();
        detachAll();
        return keyValue;
    }
}
