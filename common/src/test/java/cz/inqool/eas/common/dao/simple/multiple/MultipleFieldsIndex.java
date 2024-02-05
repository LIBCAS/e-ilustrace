package cz.inqool.eas.common.dao.simple.multiple;

import cz.inqool.eas.common.domain.index.DomainIndex;

/**
 * Index store for {@link MultipleFieldsEntity}
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
public class MultipleFieldsIndex extends DomainIndex<
        MultipleFieldsEntity,
        MultipleFieldsEntity,
        MultipleFieldsIndexedObject> {

    public MultipleFieldsIndex(Class<MultipleFieldsIndexedObject> indexedType) {
        super(indexedType);
    }
}
