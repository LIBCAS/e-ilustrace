package cz.inqool.eas.common.projection;

/**
 * Identity projection to self.
 *
 * @param <BASE> Source type
 */
public class SelfProjection<BASE extends Projectable<BASE>> implements Projection<BASE, BASE, BASE> {
    /**
     * Projects to self.
     */
    @Override
    public BASE toProjected(BASE base) {
        return base;
    }

    /**
     * Projects to self.
     */
    @Override
    public BASE toBase(BASE projected, BASE base) {
        return projected;
    }

    /**
     * Projects to self.
     */
    @Override
    public BASE toBase(BASE projected) {
        return projected;
    }

}
