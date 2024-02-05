package cz.inqool.eas.common.dao.simple.keyvalue;

import cz.inqool.eas.common.domain.store.DomainStore;

/**
 * Store for {@link SimpleKeyValueEntity}
 */
public class SimpleKeyValueStore extends DomainStore<
        SimpleKeyValueEntity,
        SimpleKeyValueEntity,
        QSimpleKeyValueEntity> {

    public SimpleKeyValueStore(Class<? extends SimpleKeyValueEntity> type) {
        super(type);
    }
}
