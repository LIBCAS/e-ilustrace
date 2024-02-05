package cz.inqool.eas.common.domain;

import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import cz.inqool.eas.common.projection.Indexed;
import cz.inqool.eas.common.projection.Projectable;

/**
 * Basic building block for every Indexed object automatically implemented by {@link DomainIndexedObject}.
 *
 * @param <ROOT> Root of the projection type system
 * @param <PROJECTED> Index projection type
 */
public interface DomainIndexed<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>> extends Indexed<ROOT, PROJECTED> {
    /**
     * Gets the id of the object
     * @return Id of the object
     */
    String getId();
}
