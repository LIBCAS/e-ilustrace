package cz.inqool.eas.common.dao.simple.dictionary;

import cz.inqool.eas.common.dictionary.index.DictionaryIndex;

/**
 * Index store for {@link SimpleDictionaryEntity}
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
public class SimpleDictionaryIndex extends DictionaryIndex<
        SimpleDictionaryEntity,
        SimpleDictionaryEntity,
        SimpleDictionaryIndexedObject> {

    public SimpleDictionaryIndex(Class<SimpleDictionaryIndexedObject> indexedType) {
        super(indexedType);
    }
}
