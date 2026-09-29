package cz.inqool.eas.common.settings.user;

import cz.inqool.eas.common.exception.dto.RestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "UserSettings", description = "UserSettings store API")
@ResponseBody
@RequestMapping("${user-settings.url}")
public class UserSettingsApi {
    private UserSettingsService service;

    @Operation(summary = "Gets the user settings of currently logged-in user")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String findUserSettings() {
        return service.findUserSettings();
    }

    @Operation(summary = "Saves user settings for currently logged-in user")
    @ApiResponse(responseCode = "200", description = "OK")
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestBody String settings) {
        service.update(settings);
    }

    @Operation(summary = "Clears user settings for currently logged-in user")
    @ApiResponse(responseCode = "200", description = "OK")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void clear() {
        service.clear();
    }

    @Autowired
    public void setService(UserSettingsService service) {
        this.service = service;
    }

}
