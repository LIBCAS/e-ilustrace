package cz.inqool.eas.common.domain.index.reindex;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reindex", description = "Reindex API")
@ResponseBody
@RequestMapping("${reindex.url}")
public class ReindexApi {
    private ReindexService service;

    @Operation(summary = "Returns all indexed repository classes.")
    @ApiResponse(responseCode = "200", description = "Success")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Class<?>> getRepositories() {
        return service.getRepositories();
    }


    @Operation(summary = "Recreates all indices in ElasticSearch. If no request params are provided, all indexed entities are reindexed.")
    @ApiResponse(responseCode = "200", description = "Success")
    @PostMapping
    public void reindex(@Schema(description = "List of fully qualified class names of stores to reindex.")
                        @RequestBody(required = false) List<String> storeClasses) {
        service.reindex(storeClasses);
    }

    @Autowired
    public void setService(ReindexService service) {
        this.service = service;
    }
}
