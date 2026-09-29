package cz.inqool.eas.common.differ.strategy.impl;

import cz.inqool.eas.common.differ.model.DiffedType;
import cz.inqool.eas.common.differ.strategy.ComparingStrategy;
import lombok.extern.slf4j.Slf4j;

// beaned in DifferConfiguration
@Slf4j
public class SimpleCollectionComparingStrategy extends CollectionComparingStrategy implements ComparingStrategy {

    @Override
    public DiffedType getType() {
        return DiffedType.SIMPLE_COLLECTION;
    }

}
