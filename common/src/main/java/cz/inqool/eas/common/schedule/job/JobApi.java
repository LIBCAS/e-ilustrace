package cz.inqool.eas.common.schedule.job;

import cz.inqool.eas.common.dictionary.DictionaryApi;
import cz.inqool.eas.common.exception.dto.RestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Schedule jobs", description = "Schedule jobs CRUD API")
@ResponseBody
@RequestMapping("${schedule.job.url}")
public class JobApi extends DictionaryApi<
        Job,
        JobDetail,
        JobList,
        JobCreate,
        JobUpdate,
        JobService> {

    @Operation(summary = "Starts non running job")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping(value = "/{id}/start")
    public void start(@PathVariable("id") String id) {
        service.start(id);
    }

    @Operation(summary = "Stops running job")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping(value = "/{id}/cancel")
    public void cancel(@PathVariable("id") String id) {
        service.stop(id);
    }

}
