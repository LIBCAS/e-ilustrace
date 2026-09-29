package cz.inqool.eas.common.dao.simple.keyvalue;

import cz.inqool.eas.common.domain.index.DomainIndex;

/**
 * Index store for {@link SimpleKeyValueEntity}
 */
public class SimpleKeyValueIndex extends DomainIndex<
        SimpleKeyValueEntity,
        SimpleKeyValueEntity,
        SimpleKeyValueIndexedObject> {

    public SimpleKeyValueIndex(Class<SimpleKeyValueIndexedObject> indexedType) {
        super(indexedType);
    }
}
