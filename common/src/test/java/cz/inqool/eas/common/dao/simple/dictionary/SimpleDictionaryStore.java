package cz.inqool.eas.common.dao.simple.dictionary;

import cz.inqool.eas.common.dictionary.store.DictionaryStore;

/**
 * Store class for {@link SimpleDictionaryEntity}
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
public class SimpleDictionaryStore extends DictionaryStore<
        SimpleDictionaryEntity,
        SimpleDictionaryEntity,
        QSimpleDictionaryEntity> {

    public SimpleDictionaryStore(Class<SimpleDictionaryEntity> type) {
        super(type);
    }
}
