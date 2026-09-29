package cz.inqool.eas.common.projection;

import cz.inqool.eas.common.exception.GeneralException;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Projection based on entity-views.
 *
 * toEntity and toView methods on entity-views will be used.
 *
 * @param <BASE>      Source type
 * @param <PROJECTED> Destination type
 */
public class ViewProjection<BASE extends Projectable<BASE>, PROJECTED extends Projectable<BASE>> implements Projection<BASE, BASE, PROJECTED> {
    private final Function<BASE, PROJECTED> toViewMethod;
    private final BiFunction<PROJECTED, BASE, BASE> toEntityMethod;
    private final Function<PROJECTED, BASE> toEntityMethodPlain;

    /**
     * Constructs view projection.
     *
     * @param baseType      Source type class
     * @param projectedType Destination type class
     */
    public ViewProjection(Class<BASE> baseType, Class<PROJECTED> projectedType) {
        this.toViewMethod = extractToView(baseType, projectedType);
        this.toEntityMethod = extractToEntity(projectedType, baseType);
        this.toEntityMethodPlain = extractToEntityPlain(projectedType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PROJECTED toProjected(BASE base) {
        return toViewMethod.apply(base);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BASE toBase(PROJECTED projected, BASE base) {
        return toEntityMethod.apply(projected, base);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BASE toBase(PROJECTED projected) {
        return toEntityMethodPlain.apply(projected);
    }

    @SuppressWarnings({"unchecked"})
    @SneakyThrows
    private Function<BASE, PROJECTED> extractToView(Class<BASE> tType, Class<PROJECTED> vType) {
        Method toView = vType.getMethod("toView", tType);
        return (o) -> {
            try {
                return (PROJECTED) toView.invoke(null, o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GeneralException("Failed to call toView method", e);
            }
        };
    }

    @SuppressWarnings({"unchecked"})
    @SneakyThrows
    private BiFunction<PROJECTED, BASE, BASE> extractToEntity(Class<PROJECTED> vType, Class<BASE> bType) {
        Method toEntity = vType.getMethod("toEntity", bType, vType);
        return (o, p) -> {
            try {
                toEntity.invoke(null, p, o);
                return p;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GeneralException("Failed to call toEntity method", e);
            }
        };
    }

    @SneakyThrows
    private Function<PROJECTED, BASE> extractToEntityPlain(Class<PROJECTED> vType) {
        Method toEntity = vType.getMethod("toEntity", vType);
        return (o) -> {
            try {
                return (BASE) toEntity.invoke(null, o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GeneralException("Failed to call toEntity method", e);
            }
        };
    }
}
