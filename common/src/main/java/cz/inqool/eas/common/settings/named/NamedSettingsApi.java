package cz.inqool.eas.common.settings.named;

import cz.inqool.eas.common.exception.dto.RestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "NamedSettings", description = "NamedSettings store API")
@ResponseBody
@RequestMapping("${named-settings.url}")
public class NamedSettingsApi {
    private NamedSettingsService service;

    @Operation(summary = "Gets the named settings of currently logged-in user for specified tag")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @GetMapping(value = "/by-tag/{tag}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NamedSettings> findUserSettings(@PathVariable("tag") String tag) {
        return service.findSettings(tag);
    }

    @Operation(summary = "Creates named settings for currently logged-in user")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public NamedSettings create(@RequestBody NamedSettingsCreate settings) {
        return service.create(settings);
    }

    @Operation(summary = "Clears named settings with specified id")
    @ApiResponse(responseCode = "200", description = "OK")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @Autowired
    public void setService(NamedSettingsService service) {
        this.service = service;
    }

}
