package cz.inqool.eas.common.differ.rest;

import cz.inqool.eas.common.differ.DifferModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// beaned in DifferConfiguration
@Tag(name = "Differ Api", description = "Differ API")
@ResponseBody
@RequestMapping("${differApi.url}")
public class DifferApi {

    protected DifferModule module;

    @Operation(summary = "List available differ mappings")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Map<String, List<String>> getMappings() {
        return module.getMappings();
    }

    @Autowired
    public void setModule(DifferModule module) {
        this.module = module;
    }

}
