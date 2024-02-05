package cz.inqool.eas.common.dated;

import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.projection.Projectable;

import java.time.Instant;

/**
 * Interface representing indexed objects aware of their created,
 * updated and deleted state (aren't removed permanently on delete).
 *
 * @param <ROOT>      Root of the projection type system
 * @param <PROJECTED> Index projection type
 */

public interface DatedIndexed<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>> extends DomainIndexed<ROOT, PROJECTED> {
    /**
     * Gets created date.
     *
     * @return Date
     */
    Instant getCreated();

    /**
     * Gets date of last update.
     *
     * @return Date
     */
    Instant getUpdated();

    /**
     * Gets date of deletion.
     *
     * @return Date
     */
    Instant getDeleted();
}
