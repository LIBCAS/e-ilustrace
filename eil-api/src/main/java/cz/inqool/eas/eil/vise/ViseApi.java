package cz.inqool.eas.eil.vise;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cz.inqool.eas.eil.config.swagger.HttpStatus.OK;

@Tag(name = "Vise API", description = "Api for working with VISE metadata")
@RestController
@RequestMapping("/vise")
public class ViseApi {

    private ViseManager viseManager;

    @Operation(summary = "Populate SQLite DB for VISE with metadata")
    @ApiResponse(responseCode = OK, description = "OK")
    @GetMapping("/populate")
    public void populateAndAlternateDb() {
        viseManager.populateAndAlternateDb();
    }

    @Operation(summary = "Remove Illustration images from Vise")
    @ApiResponse(responseCode = OK, description = "OK")
    @GetMapping("/delete")
    public void deleteFilesFromVise() {
        viseManager.deleteFromVise();
    }

    @Operation(summary = "Move Illustration photos to VISE directory")
    @ApiResponse(responseCode = OK, description = "OK")
    @GetMapping("/copy")
    public void moveFilesToVise() {
        viseManager.moveFilesToVise();
    }

    @Operation(summary = "Set VISE file ids")
    @ApiResponse(responseCode = OK, description = "OK")
    @GetMapping("/set")
    public void setViseFileIds() {
        viseManager.setViseFileIds();
    }

    @Autowired
    public void setViseManager(ViseManager viseManager) {
        this.viseManager = viseManager;
    }
}
