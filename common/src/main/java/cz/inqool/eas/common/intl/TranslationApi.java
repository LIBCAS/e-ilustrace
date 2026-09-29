package cz.inqool.eas.common.intl;

import cz.inqool.eas.common.dictionary.DictionaryApi;
import cz.inqool.eas.common.exception.dto.RestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Tag(name = "Translations", description = "Translations CRUD API")
@ResponseBody
@RequestMapping("${intl.translation.url}")
public class TranslationApi extends DictionaryApi<
        Translation,
        TranslationDetail,
        TranslationList,
        TranslationCreate,
        TranslationUpdate,
        TranslationService> {

    @Operation(summary = "Get the content of a translation", description = "Returns content of an upload in input stream.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "The file was not found.", content = @Content(schema = @Schema(implementation = RestException.class)))
    @GetMapping(value = "/load/{lang}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> load(@PathVariable("lang") Language lang) {
        return service.load(lang);
    }
}
