package cz.inqool.eas.eil.iconclass;

import cz.inqool.eas.common.domain.DomainApi;
import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Iconclass API", description = "Api for working with iconclass categories")
@RestController
@RequestMapping("/iconclass")
public class IconclassApi extends DomainApi<
        IconclassCategory,
        IconclassCategoryDefault,
        IconclassCategoryDefault,
        IconclassCategoryCreate,
        IconclassCategoryDefault,
        IconclassService
        > {
    @Operation(summary = "Create new Iconclass category in EIL. Only 'code' attribute is sent from FE, other attributes are computed on BE.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Wrong input was specified", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping
    @Override
    public IconclassCategoryDefault create(@Valid @RequestBody IconclassCategoryCreate view) {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        return service.create(view);
    }
}
