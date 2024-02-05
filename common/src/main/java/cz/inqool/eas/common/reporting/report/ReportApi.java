package cz.inqool.eas.common.reporting.report;

import cz.inqool.eas.common.dictionary.index.DictionaryAutocomplete;
import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.common.reporting.dto.ReportDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Generated reports", description = "Generated reports")
@ResponseBody
@RequestMapping("${reporting.url}")
public class ReportApi {

    private ReportService service;

    @Operation(summary = "List allowed report definitions")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping("/definitions")
    public List<ReportDefinition> getDefinitions() {
        return service.getAllowedDefinitions();
    }

    @Operation(summary = "List all report definitions for autocomplete")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping("/definitions/autocomplete")
    public List<DictionaryAutocomplete> getDefinitionsAutocomplete() {
        return service.getDefinitionsAutocomplete();
    }


    @Operation(summary = "Get report for specified definition")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "no generated report found for specified attribute", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @GetMapping("/{definitionId}")
    public ReportDetail get(@PathVariable("definitionId") String definitionId) {
        return service.getReportByDefinition(definitionId);
    }

    @Operation(summary = "Generates the report")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Report attribute is not allowed for user", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "404", description = "Report attribute was not found", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @PostMapping(value = "/{code}/generate")
    public ReportDetail generate(@PathVariable("code") String code, @RequestBody Map<String, Object> configuration) {
        return service.generate(code, configuration);
    }

    @Autowired
    public void setService(ReportService service) {
        this.service = service;
    }
}
