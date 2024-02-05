package cz.inqool.eas.common.dao.simple.multiple;

import cz.inqool.eas.common.domain.DomainRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class for {@link MultipleFieldsEntity}
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
@Repository
public class MultipleFieldsRepository extends DomainRepository<
        MultipleFieldsEntity,
        MultipleFieldsEntity,
        MultipleFieldsIndexedObject,
        MultipleFieldsStore,
        MultipleFieldsIndex> {
}
