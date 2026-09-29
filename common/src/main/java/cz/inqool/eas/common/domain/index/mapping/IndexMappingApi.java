package cz.inqool.eas.common.domain.index.mapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "IndexMappings", description = "Index Mappings API")
@ResponseBody
@RequestMapping("${indexMapping.url}")
public class IndexMappingApi {

    private IndexMappingService service;

    @Operation(summary = "List available filterable/sortable index mappings")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Map<String, List<String>> getMappings(@RequestBody(required = false) IndexMappingDto dto) {
        return service.getMappings(dto != null
                ? dto.entityNames
                : Collections.emptyList()
        );
    }

    @Autowired
    public void setService(IndexMappingService service) {
        this.service = service;
    }

    @Data
    protected static class IndexMappingDto {
        @ArraySchema(schema = @Schema(example = "Sequence", description = "Entity name"))
        List<String> entityNames = new ArrayList<>();
    }
}
