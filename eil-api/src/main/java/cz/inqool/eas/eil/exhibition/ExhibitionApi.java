package cz.inqool.eas.eil.exhibition;

import cz.inqool.eas.common.dated.DatedApi;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.eil.exhibition.item.ExhibitionItemRef;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "Exhibition API", description = "Api for working with exhibitions")
@RestController
@RequestMapping("/exhibition")
public class ExhibitionApi extends DatedApi<
        Exhibition,
        ExhibitionDetail,
        ExhibitionList,
        ExhibitionCreate,
        ExhibitionUpdate,
        ExhibitionService> {

    @Operation(summary = "Delete Exhibition items from Exhibition")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PutMapping("/{id}/delete-items")
    public ExhibitionDetail deleteItems(@NotNull @PathVariable("id") String id, @Valid @RequestBody List<ExhibitionItemRef> items) {
        return service.deleteItems(id, items);
    }

    @Operation(summary = "Creates duplicate Exhibition")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping("/{id}/duplicate")
    public ExhibitionDetail duplicate(@NotNull @PathVariable("id") String id) {
        return service.duplicate(id);
    }

    @Operation(summary = "List only Exhibitions created by current user")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping("/list-mine")
    public Result<ExhibitionList> listMine(@Valid @RequestBody Params params) {
        return service.listMine(params);
    }

    @Operation(summary = "Publish exhibition")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PutMapping("/{id}/publish")
    public ExhibitionDetail publish(@NotNull @PathVariable("id") String id) {
        return service.publish(id);
    }
}
