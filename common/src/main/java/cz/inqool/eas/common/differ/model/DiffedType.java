package cz.inqool.eas.common.differ.model;

import cz.inqool.eas.common.domain.Domain;

public enum DiffedType {
    SIMPLE,
    /**
     * Collection of Strings, Integers, LocalDates ...
     */
    SIMPLE_COLLECTION,
    /**
     * Class implementing {@link Domain}
     */
    ENTITY,
    /**
     * Collection of classes implementing {@link Domain}
     */
    ENTITY_COLLECTION,
    EMBEDDED,
}
