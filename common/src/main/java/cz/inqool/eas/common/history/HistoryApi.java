package cz.inqool.eas.common.history;

import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "History", description = "History API")
@ResponseBody
@RequestMapping("${history.url}")
public class HistoryApi {
    private HistoryService service;

    @Operation(summary = "List objects satisfying given params")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/{entityId}/list")
    public Result<HistoryList> listForEntity(@PathVariable("entityId") String entityId, @Valid @RequestBody(required = false) Params params) {
        return service.listForEntity(entityId, params);
    }

    @Operation(summary = "List objects satisfying given params")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/{entityId}/full")
    public List<HistoryList> allForEntity(@PathVariable("entityId") String entityId) {
        return service.fullForEntity(entityId);
    }

    @Autowired
    public void setService(HistoryService service) {
        this.service = service;
    }
}
