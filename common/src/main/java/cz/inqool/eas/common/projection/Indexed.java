package cz.inqool.eas.common.projection;

/**
 * Marking interface for indexed types.
 *
 * Requiring to implement toIndexedObject method for converting from PROJECTED type.
 *
 * @param <ROOT>      Root of the projection type system
 * @param <PROJECTED> Index projection type
 */
public interface Indexed<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>> extends Projectable<ROOT> {
    void toIndexedObject(PROJECTED obj);
}
