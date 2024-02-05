package cz.inqool.eas.common.dated;

import cz.inqool.eas.common.domain.DomainService;
import cz.inqool.eas.common.projection.Projectable;

/**
 * CRUD Service layer for objects implementing {@link Dated}.
 *
 * @param <ROOT>              Root of the projection type system
 * @param <DETAIL_PROJECTION> Projection used for retrieving object detail
 * @param <LIST_PROJECTION>   Projection used for listing object
 * @param <CREATE_PROJECTION> Projection used as DTO for object creation
 * @param <UPDATE_PROJECTION> Projection used as DTO for object update
 * @param <REPOSITORY>        Repository type
 *
 * fixme: add methods for retrieval of deleted objects and for restoring objects
 */
public abstract class DatedService<
        ROOT extends Dated<ROOT>,
        DETAIL_PROJECTION extends Dated<ROOT>,
        LIST_PROJECTION extends Dated<ROOT>,
        CREATE_PROJECTION extends Projectable<ROOT>,
        UPDATE_PROJECTION extends Projectable<ROOT>,
        REPOSITORY extends DatedRepository<ROOT, ?, ?, ?, ?>
        > extends DomainService<
        ROOT,
        DETAIL_PROJECTION,
        LIST_PROJECTION,
        CREATE_PROJECTION,
        UPDATE_PROJECTION,
        REPOSITORY> {
}
