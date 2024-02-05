package cz.inqool.eas.common.dao.simple.keyvalue;

import cz.inqool.eas.common.domain.DomainRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link SimpleKeyValueEntity}
 */
@Repository
public class SimpleKeyValueRepository extends DomainRepository<
        SimpleKeyValueEntity,
        SimpleKeyValueEntity,
        SimpleKeyValueIndexedObject,
        SimpleKeyValueStore,
        SimpleKeyValueIndex> {}
