package cz.inqool.eas.common.authored;

import cz.inqool.eas.common.dated.DatedApi;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.projection.Projectable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * CRUD API layer for authored objects.
 *
 * @param <ROOT>              Root of the projection type system
 * @param <DETAIL_PROJECTION> Projection used for retrieving object detail
 * @param <LIST_PROJECTION>   Projection used for listing object
 * @param <CREATE_PROJECTION> Projection used as DTO for object creation
 * @param <UPDATE_PROJECTION> Projection used as DTO for object update
 * @param <SERVICE>           Service type
 */
public abstract class AuthoredApi<
        ROOT extends Authored<ROOT>,
        DETAIL_PROJECTION extends Authored<ROOT>,
        LIST_PROJECTION extends Authored<ROOT>,
        CREATE_PROJECTION extends Projectable<ROOT>,
        UPDATE_PROJECTION extends Projectable<ROOT>,
        SERVICE extends AuthoredService<ROOT, DETAIL_PROJECTION, LIST_PROJECTION, CREATE_PROJECTION, UPDATE_PROJECTION, ?>>
        extends DatedApi<
        ROOT,
        DETAIL_PROJECTION,
        LIST_PROJECTION,
        CREATE_PROJECTION,
        UPDATE_PROJECTION,
        SERVICE> {

    @Operation(summary = "List objects satisfying given params created by current user")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/list/mine")
    public Result<LIST_PROJECTION> listMine(@Valid @RequestBody(required = false) Params params) {
        return service.listMine(params);
    }

    @Operation(summary = "List objects satisfying given params created by user in current tenant")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/list/our")
    public Result<LIST_PROJECTION> listOur(@Valid @RequestBody(required = false) Params params) {
        return service.listOur(params);
    }
}
