package cz.inqool.eas.common.projection;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;

/**
 * Projection based on indexed object.
 *
 * toIndexedObject method on indexed object will be used.
 *
 * @param <ROOT>    Root of the projection type system
 * @param <BASE>    Source type
 * @param <INDEXED> Destination type
 */
public class IndexableProjection<ROOT extends Projectable<ROOT>, BASE extends Projectable<ROOT>, INDEXED extends Indexed<ROOT, BASE>> implements Projection<ROOT, BASE, INDEXED> {
    private Constructor<INDEXED> constructor;

    /**
     * Constructs Indexed projection.
     *
     * @param indexedType Destination type class
     */
    @SneakyThrows
    public IndexableProjection(Class<INDEXED> indexedType) {
        this.constructor = indexedType.getDeclaredConstructor();
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public INDEXED toProjected(BASE root) {
        INDEXED indexed = constructor.newInstance();
        indexed.toIndexedObject(root);
        return indexed;
    }

    /**
     * Back projecting is not supported.
     *
     * @throws UnsupportedOperationException Always
     */
    @Override
    public BASE toBase(INDEXED projected, BASE base) {
        throw new UnsupportedOperationException("Projecting indexed object back to root type is not supported");
    }

    /**
     * Back projecting is not supported.
     *
     * @throws UnsupportedOperationException Always
     */
    @Override
    public BASE toBase(INDEXED projected) {
        throw new UnsupportedOperationException("Projecting indexed object back to root type is not supported");
    }
}
