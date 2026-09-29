package cz.inqool.eas.common.settings.app;

import cz.inqool.eas.common.exception.dto.RestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AppSettings", description = "AppSettings store API")
@ResponseBody
@RequestMapping("${app-settings.url}")
public class AppSettingsApi {
    private AppSettingsService service;

    @Operation(summary = "Gets the app settings")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String get() {
        return service.get();
    }

    @Operation(summary = "Saves app settings")
    @ApiResponse(responseCode = "200", description = "OK")
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestBody String settings) {
        service.update(settings);
    }

    @Autowired
    public void setService(AppSettingsService service) {
        this.service = service;
    }

}
