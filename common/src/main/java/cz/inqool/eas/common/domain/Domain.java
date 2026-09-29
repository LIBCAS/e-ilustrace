package cz.inqool.eas.common.domain;

import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.common.projection.Projectable;

/**
 * Basic building block for every JPA entity automatically implemented by {@link DomainObject}.
 *
 * @param <ROOT> Root of the projection type system
 */
public interface Domain<ROOT> extends Projectable<ROOT> {

    /**
     * Returns the id of an object.
     */
    String getId();

    /**
     * Sets the id of the object.
     *
     * @param id Id to set
     */
    void setId(String id);
}
