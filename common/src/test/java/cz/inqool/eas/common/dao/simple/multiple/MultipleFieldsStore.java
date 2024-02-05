package cz.inqool.eas.common.dao.simple.multiple;

import cz.inqool.eas.common.domain.store.DomainStore;

/**
 * Store class for {@link MultipleFieldsEntity}
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
public class MultipleFieldsStore extends DomainStore<
        MultipleFieldsEntity,
        MultipleFieldsEntity,
        QMultipleFieldsEntity> {

    public MultipleFieldsStore(Class<? extends MultipleFieldsEntity> type) {
        super(type);
    }
}
