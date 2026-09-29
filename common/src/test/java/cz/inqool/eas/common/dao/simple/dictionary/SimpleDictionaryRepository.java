package cz.inqool.eas.common.dao.simple.dictionary;

import cz.inqool.eas.common.dictionary.DictionaryRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link SimpleDictionaryEntity}
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
@Repository
public class SimpleDictionaryRepository extends DictionaryRepository<
        SimpleDictionaryEntity,
        SimpleDictionaryEntity,
        SimpleDictionaryIndexedObject,
        SimpleDictionaryStore,
        SimpleDictionaryIndex> {
}
