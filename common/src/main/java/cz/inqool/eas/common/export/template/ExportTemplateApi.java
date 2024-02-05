package cz.inqool.eas.common.export.template;

import cz.inqool.eas.common.dictionary.DictionaryApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Export templates", description = "Export templates CRUD API")
@ResponseBody
@RequestMapping("${export.url}/templates")
public class ExportTemplateApi extends DictionaryApi<
        ExportTemplate,
        ExportTemplateDetail,
        ExportTemplateList,
        ExportTemplateCreate,
        ExportTemplateUpdate,
        ExportTemplateService> {

    @Operation(summary = "List objects tagged with specified tag")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/tagged/{tag}")
    public List<ExportTemplateList> listByTag(@PathVariable("tag") String tag) {
        return service.listByTag(tag);
    }
}
