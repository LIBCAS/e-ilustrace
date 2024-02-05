package cz.inqool.eas.common.export.request;

import cz.inqool.eas.common.authored.AuthoredApi;
import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.common.export.request.dto.FinishProcessingDto;
import cz.inqool.eas.common.export.request.dto.SignalErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Export requests", description = "Export requests CRUD API")
@ResponseBody
@RequestMapping("${export.url}/requests")
public class ExportRequestApi extends AuthoredApi<
        ExportRequest,
        ExportRequestDetail,
        ExportRequestList,
        ExportRequestCreate,
        ExportRequestUpdate,
        ExportRequestService> {

    @Operation(summary = "Acquires next export to process")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/acquire")
    public ExportRequest acquire() {
        return service.acquire();
    }

    @Operation(summary = "Acquires next export to process")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/{id}/acquire")
    public ExportRequest acquireExact(@PathVariable("id") String id) {
        return service.acquireExact(id);
    }


    @Operation(summary = "Starts non running job")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping(value = "/{id}/finish-processing")
    public void finishProcessing(@PathVariable("id") String id, @RequestBody FinishProcessingDto dto) {
        service.finishProcessing(id, dto);
    }

    @Operation(summary = "Starts non running job")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping(value = "/{id}/signal-error")
    public void signalError(@PathVariable("id") String id, @RequestBody SignalErrorDto dto) {
        service.signalError(id, dto);
    }
}
