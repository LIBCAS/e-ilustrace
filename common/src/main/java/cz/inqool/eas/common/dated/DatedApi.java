package cz.inqool.eas.common.dated;

import cz.inqool.eas.common.domain.DomainApi;
import cz.inqool.eas.common.projection.Projectable;

/**
 * CRUD API layer for dated objects.
 *
 * @param <ROOT>              Root of the projection type system
 * @param <DETAIL_PROJECTION> Projection used for retrieving object detail
 * @param <LIST_PROJECTION>   Projection used for listing object
 * @param <CREATE_PROJECTION> Projection used as DTO for object creation
 * @param <UPDATE_PROJECTION> Projection used as DTO for object update
 * @param <SERVICE>>          Service type
 *
 * fixme: add methods for retrieval of deleted objects and for restoring objects
 */
public abstract class DatedApi<
        ROOT extends Dated<ROOT>,
        DETAIL_PROJECTION extends Dated<ROOT>,
        LIST_PROJECTION extends Dated<ROOT>,
        CREATE_PROJECTION extends Projectable<ROOT>,
        UPDATE_PROJECTION extends Projectable<ROOT>,
        SERVICE extends DatedService<ROOT, DETAIL_PROJECTION, LIST_PROJECTION, CREATE_PROJECTION, UPDATE_PROJECTION, ?>>
        extends DomainApi<
        ROOT,
        DETAIL_PROJECTION,
        LIST_PROJECTION,
        CREATE_PROJECTION,
        UPDATE_PROJECTION,
        SERVICE> {
}
