package cz.inqool.eas.common.projection;

/**
 * Projection from one type of projection hierarchy to another.
 *
 * @param <ROOT>      Root of the projection type system
 * @param <BASE>      Source type
 * @param <PROJECTED> Destination type
 */
public interface Projection<ROOT extends Projectable<ROOT>, BASE extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>> {
    /**
     * Projects the object of base type onto another type.
     *
     * @param base Object of base type
     * @return Object of projected type
     */
    PROJECTED toProjected(BASE base);

    /**
     * Projects back the object of projected type onto the base type.
     *
     * @param projected Object of projected type
     * @return Object of root type
     */
    BASE toBase(PROJECTED projected, BASE base);

    /**
     * Projects back the object of projected type onto the base type.
     *
     * @param projected Object of projected type
     * @return Object of root type
     */
    BASE toBase(PROJECTED projected);
}
