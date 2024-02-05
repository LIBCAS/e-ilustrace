package cz.inqool.eas.common.dated;

import cz.inqool.eas.common.domain.Domain;

import java.time.Instant;

/**
 * Interface representing objects aware of their created,
 * updated and deleted state (aren't removed permanently on delete).
 *
 * @param <ROOT> Root of the projection type system
 */
public interface Dated<ROOT> extends Domain<ROOT> {
    /**
     * Sets created date.
     *
     * The date will be set automatically.
     *
     * @param date Date to set
     */
    void setCreated(Instant date);

    /**
     * Sets updated date.
     *
     * The date will be set automatically.
     *
     * @param date Date to set
     */
    void setUpdated(Instant date);

    /**
     * Sets deleted date.
     *
     * The date will be set automatically.
     *
     * @param date Date to set
     */
    void setDeleted(Instant date);

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
