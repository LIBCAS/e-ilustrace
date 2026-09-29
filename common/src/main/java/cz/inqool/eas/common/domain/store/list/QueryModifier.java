package cz.inqool.eas.common.domain.store.list;

import com.querydsl.jpa.impl.JPAQuery;
import cz.inqool.eas.common.projection.Projectable;

/**
 * Query modifier functional interface.
 * @param <ROOT> Root of the projection type system
 * @param <PROJECTED> Projection type
 */
@FunctionalInterface
public interface QueryModifier<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>> {
    void modify(JPAQuery<PROJECTED> query);
}
