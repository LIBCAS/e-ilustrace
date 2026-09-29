package cz.inqool.eas.eil.theme;

import cz.inqool.eas.common.dated.DatedApi;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.eil.theme.dto.ThemeCount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static cz.inqool.eas.eil.config.swagger.HttpStatus.OK;

@Tag(name = "Theme API", description = "Api for working with custom EIL themes")
@RestController
@RequestMapping("/theme")
public class ThemeApi extends DatedApi<
        Theme,
        ThemeDefault,
        ThemeDefault,
        ThemeCreate,
        ThemeDefault,
        ThemeService
        > {

    @Operation(summary = "Count occurrences of themes")
    @ApiResponse(responseCode = OK, description = "OK")
    @GetMapping("/count")
    public List<ThemeCount> count() {
        return service.countThemes();
    }

    @Operation(summary = "Count occurrences of themes by params")
    @ApiResponse(responseCode = OK, description = "OK")
    @PostMapping("/count-by-params")
    public List<ThemeCount> countByParams(@Valid @RequestBody(required = false) Params params) {
        return service.countThemesByParams(params);
    }
}
