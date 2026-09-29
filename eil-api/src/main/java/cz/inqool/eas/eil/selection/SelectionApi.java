package cz.inqool.eas.eil.selection;

import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.eil.selection.dto.SelectionDto;
import cz.inqool.eas.eil.selection.dto.SelectionUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Selection API", description = "Api for working with selection")
@RestController
@RequestMapping("/selection")
public class SelectionApi {

    private SelectionService selectionService;

    @Operation(summary = "Add selection Illustrations and Books")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping("/add-items")
    public void addItems(@Valid @RequestBody SelectionUpdateDto dto) {
        selectionService.addItems(dto);
    }

    @Operation(summary = "Delete selection Illustrations and Books")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PutMapping("/delete-items")
    public void deleteItems(@Valid @RequestBody SelectionDto dto) {
        selectionService.deleteItems(dto);
    }

    @Operation(summary = "Get selection for logged user")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @GetMapping("/mine")
    public SelectionDetail getMine() {
        return selectionService.getMine();
    }

    @Operation(summary = "Set Mirador flag to Selection items")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PutMapping("/mirador")
    public void setMirador(@Valid @RequestBody SelectionUpdate dto) {
        selectionService.setMiradorFlags(dto);
    }

    @Autowired
    public void setSelectionService(SelectionService selectionService) {
        this.selectionService = selectionService;
    }
}
