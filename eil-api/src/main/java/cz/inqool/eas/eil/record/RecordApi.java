package cz.inqool.eas.eil.record;

import cz.inqool.eas.common.dated.DatedApi;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.eil.download.ImageForDownload;
import cz.inqool.eas.eil.mirador.MiradorService;
import cz.inqool.eas.eil.mirador.dto.Manifest;
import cz.inqool.eas.eil.record.dto.RecordFacetsDto;
import cz.inqool.eas.eil.record.dto.RecordTypeDto;
import cz.inqool.eas.eil.record.dto.RecordYearsDto;
import cz.inqool.eas.eil.record.illustration.IconclassThemeState;
import cz.inqool.eas.eil.record.illustration.IllustrationDetail;
import cz.inqool.eas.eil.record.illustration.xlsx.XlsxReader;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static cz.inqool.eas.eil.config.swagger.HttpStatus.OK;

@Tag(name = "Record API", description = "Api for working with illustration and book records")
@RestController
@RequestMapping("/record")
public class RecordApi extends DatedApi<
        Record,
        RecordDetail,
        RecordList,
        RecordCreate,
        RecordUpdate,
        RecordService
        > {

    private XlsxReader xlsxReader;
    private MiradorService miradorService;

    @Operation(summary = "Import Illustrations data from .xlsx")
    @ApiResponse(responseCode = OK, description = "OK")
    @PostMapping("/ill/import")
    public boolean importIlls() {
        return xlsxReader.readIllustrations();
    }

    @Operation(summary = "Change Iconclass state of Illustration")
    @ApiResponse(responseCode = OK, description = "OK")
    @PutMapping("/{id}/ill/iconclass-state")
    public IllustrationDetail setIconclassState(@NotNull @PathVariable String id, @Valid @RequestBody IconclassThemeState state) {
        return service.setIconclassState(id, state);
    }

    @Operation(summary = "Change Theme state of Illustration")
    @ApiResponse(responseCode = OK, description = "OK")
    @PutMapping("/{id}/ill/theme-state")
    public IllustrationDetail setThemeState(@NotNull @PathVariable String id, @Valid @RequestBody IconclassThemeState state) {
        return service.setThemeState(id, state);
    }

    @Operation(summary = "Move Illustration images to Cantaloupe")
    @ApiResponse(responseCode = OK, description = "OK")
    @GetMapping("/cantaloupe-copy")
    public void moveFilesToCantaloupe() {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        miradorService.moveIllsImagesToCantaloupe();
    }

    @Operation(summary = "Remove Illustration images from Cantaloupe")
    @ApiResponse(responseCode = OK, description = "OK")
    @GetMapping("/cantaloupe-delete")
    public void deleteFilesFromCantaloupe() {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        miradorService.deleteFromCantaloupe();
    }

    @Operation(summary = "Set Cantaloupe IDs to Illustrations")
    @ApiResponse(responseCode = OK, description = "OK")
    @GetMapping("/cantaloupe-set")
    public void setCantaloupeIds() {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        miradorService.setCantaloupeIds();
    }

    @Operation(summary = "Remove files from Cantaloupe, reset IDs and flags, copy files and index.")
    @ApiResponse(responseCode = OK, description = "OK")
    @GetMapping("/cantaloupe-reset")
    public void resetMiradorImages() {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        miradorService.resetMiradorImages();
    }

    @Operation(summary = "Get Mirador manifest")
    @ApiResponse(responseCode = OK, description = "OK")
    @CrossOrigin
    @GetMapping("/{id}/manifest.json")
    public Manifest getManifest(@NotNull @PathVariable String id) {
        return service.getManifest(id);
    }

    @Operation(summary = "Get years range for Record")
    @ApiResponse(responseCode = OK, description = "OK")
    @PostMapping("/years-range")
    public RecordYearsDto getYearsRange(@Valid @RequestBody RecordTypeDto dto) {
        return service.getYearsRange(dto.getType());
    }

    @Operation(summary = "Filter facets")
    @ApiResponse(responseCode = OK, description = "OK")
    @PostMapping("/list-facets/{type}")
    public RecordFacetsDto listFacets(@NotNull @PathVariable String type,
                                      @Valid @RequestBody(required = false) Params params) {
        return service.listFacets(params, type);
    }

    @Operation(summary = "Delete Record image")
    @ApiResponse(responseCode = OK, description = "OK")
    @PutMapping("/{id}/delete-image")
    public void deleteImage(@NotNull @PathVariable String id,
                            @Valid @RequestBody ImageForDownload imageType) {
        service.deleteImage(id, imageType);
    }

    @Autowired
    public void setXlsxReader(XlsxReader xlsxReader) {
        this.xlsxReader = xlsxReader;
    }

    @Autowired
    public void setMiradorService(MiradorService miradorService) {
        this.miradorService = miradorService;
    }
}
