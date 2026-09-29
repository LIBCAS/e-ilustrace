package cz.inqool.eas.common.domain;

import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.projection.Projectable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * CRUD API layer for domain objects.
 *
 * @param <ROOT>              Root of the projection type system
 * @param <DETAIL_PROJECTION> Projection used for retrieving object detail
 * @param <LIST_PROJECTION>   Projection used for listing object
 * @param <CREATE_PROJECTION> Projection used as DTO for object creation
 * @param <UPDATE_PROJECTION> Projection used as DTO for object update
 * @param <SERVICE>         Service type
 */
public abstract class DomainApi<
        ROOT extends Domain<ROOT>,
        DETAIL_PROJECTION extends Domain<ROOT>,
        LIST_PROJECTION extends Domain<ROOT>,
        CREATE_PROJECTION extends Projectable<ROOT>,
        UPDATE_PROJECTION extends Projectable<ROOT>,
        SERVICE extends DomainService<ROOT, DETAIL_PROJECTION, LIST_PROJECTION, CREATE_PROJECTION, UPDATE_PROJECTION, ?>> {

    protected SERVICE service;

    @Operation(summary = "Get object with given id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @GetMapping(value = "/{id}")
    public DETAIL_PROJECTION get(@PathVariable("id") String id) {
        return service.get(id);
    }

    @Operation(summary = "Creates new object")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Wrong input was specified", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping
    public DETAIL_PROJECTION create(@Valid @RequestBody CREATE_PROJECTION view) {
        return service.create(view);
    }

    @Operation(summary = "Updates existing object")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object was not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "400", description = "Wrong input was specified", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PutMapping("/{id}")
    public DETAIL_PROJECTION update(@PathVariable("id") String id, @Valid @RequestBody UPDATE_PROJECTION view) {
        return service.update(id, view);
    }

    @Operation(summary = "Delete object with given id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Dictionary not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @Operation(summary = "List objects satisfying given params")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/list")
    public Result<LIST_PROJECTION> list(@Valid @RequestBody(required = false) Params params) {
        return service.list(params);
    }

    @Autowired
    public void setService(SERVICE service) {
        this.service = service;
    }
}
