package cz.inqool.eas.common.dictionary;

import cz.inqool.eas.common.authored.AuthoredApi;
import cz.inqool.eas.common.dictionary.index.DictionaryAutocomplete;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.common.intl.Language;
import cz.inqool.eas.common.projection.Projectable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * CRUD API layer for dictionary objects.
 *
 * @param <ROOT>              Root of the projection type system
 * @param <DETAIL_PROJECTION> Projection used for retrieving object detail
 * @param <LIST_PROJECTION>   Projection used for listing object
 * @param <CREATE_PROJECTION> Projection used as DTO for object creation
 * @param <UPDATE_PROJECTION> Projection used as DTO for object update
 * @param <SERVICE>           Service type
 *
 * fixme: support for duplicates and replacing objects
 */
public abstract class DictionaryApi<
        ROOT extends Dictionary<ROOT>,
        DETAIL_PROJECTION extends Dictionary<ROOT>,
        LIST_PROJECTION extends Dictionary<ROOT>,
        CREATE_PROJECTION extends Projectable<ROOT>,
        UPDATE_PROJECTION extends Projectable<ROOT>,
        SERVICE extends DictionaryService<
                ROOT,
                DETAIL_PROJECTION,
                LIST_PROJECTION,
                CREATE_PROJECTION,
                UPDATE_PROJECTION,
                ?
                >
        >
        extends AuthoredApi<
        ROOT,
        DETAIL_PROJECTION,
        LIST_PROJECTION,
        CREATE_PROJECTION,
        UPDATE_PROJECTION,
        SERVICE> {

    @Operation(summary = "Get object by code")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @GetMapping(value = "/coded/{code}")
    public DETAIL_PROJECTION getByCode(@PathVariable("code") String code) {
        return service.getByCode(code);
    }

    @Operation(summary = "Deactivates object with given id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @DeleteMapping(value = "/{id}/active")
    public void deactivate(@PathVariable("id") String id) {
        service.deactivate(id);
    }

    @Operation(summary = "Activate dictionary with given id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PutMapping(value = "/{id}/active")
    public void activate(@PathVariable("id") String id) {
        service.activate(id);
    }

    @Operation(summary = "List active valid objects satisfying given params and query string")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/autocomplete")
    public Result<DictionaryAutocomplete> autocomplete(@RequestParam(value = "query", required = false, defaultValue = "") String query,
                                                       @RequestParam(value = "language", required = false) Language language,
                                                       @Valid @RequestBody(required = false) Params params) {
        return service.listAutocomplete(query, language, params);
    }

    @Operation(summary = "List all objects satisfying given params and query string")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/autocomplete/all")
    public Result<DictionaryAutocomplete> autocompleteAll(@RequestParam(value = "query", required = false, defaultValue = "") String query,
                                                       @RequestParam(value = "language", required = false) Language language,
                                                       @Valid @RequestBody(required = false) Params params) {
        return service.listAutocompleteAll(query, language, params);
    }

    @Operation(summary = "List all active valid objects satisfying given params and query string")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/autocomplete/full")
    public List<DictionaryAutocomplete> autocompleteFull(@RequestParam(value = "query", required = false, defaultValue = "") String query,
                                                         @RequestParam(value = "language", required = false) Language language,
                                                         @Valid @RequestBody(required = false) Params params) {
        return service.listAutocompleteFull(query, language, params);
    }

    @Operation(summary = "List all objects satisfying given params and query string")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/autocomplete/full/all")
    public List<DictionaryAutocomplete> autocompleteFullAll(@RequestParam(value = "query", required = false, defaultValue = "") String query,
                                                         @RequestParam(value = "language", required = false) Language language,
                                                         @Valid @RequestBody(required = false) Params params) {
        return service.listAutocompleteFullAll(query, language, params);
    }
}
