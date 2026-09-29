package cz.inqool.eas.common.domain.store.list;

import cz.inqool.eas.common.projection.Projectable;

import java.util.List;

/**
 * List function functional interface.
 * @param <ROOT> Root of the projection type system
 * @param <PROJECTED> Projection type
 */
@FunctionalInterface
public interface ListFunction<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>> {
    List<PROJECTED> call(QueryModifier<ROOT, PROJECTED> modifier);
}
