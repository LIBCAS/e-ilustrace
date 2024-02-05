package cz.inqool.eas.common.signing.request;

import cz.inqool.eas.common.authored.AuthoredApi;
import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.common.signing.request.dto.UploadSignedContentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Sign requests", description = "Sign requests CRUD API")
@ResponseBody
@RequestMapping("${signing.url}/requests")
public class SignRequestApi extends AuthoredApi<
        SignRequest,
        SignRequestDetail,
        SignRequestList,
        SignRequestCreate,
        SignRequestUpdate,
        SignRequestService> {

    @Operation(summary = "Uploads signed content")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping(value = "/{id}/upload-signed-content")
    public void uploadSignedContent(@PathVariable("id") String id, @Valid @RequestBody UploadSignedContentDto dto) {
        service.uploadSignedFile(id, dto);
    }

    @Operation(summary = "Marks the request signed")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping(value = "/{id}/sign")
    public void sign(@PathVariable("id") String id) {
        service.sign(id);
    }
}
