package cz.inqool.eas.common.state.app;

import cz.inqool.eas.common.exception.dto.RestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AppState", description = "AppState store API")
@ResponseBody
@RequestMapping("${app-state.url}")
public class AppStateApi {
    private AppStateService service;

    @Operation(summary = "Gets the app state")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String get() {
        return service.get();
    }

    @Operation(summary = "Saves app state")
    @ApiResponse(responseCode = "200", description = "OK")
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestBody String state) {
        service.update(state);
    }

    @Autowired
    public void setService(AppStateService service) {
        this.service = service;
    }

}
