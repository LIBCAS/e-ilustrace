package cz.inqool.eas.common.security.personal;

import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Tag(name = "Personal events", description = "Personal Event API")
@ResponseBody
@RequestMapping("${personalEvent.url}")
public class PersonalEventApi {
    private PersonalEventService service;

    @Operation(summary = "List objects satisfying given params created by current user")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/list/mine")
    public Result<PersonalEventList> listMine(@Valid @RequestBody(required = false) Params params) {
        return service.listMine(params);
    }

    @Autowired
    public void setService(PersonalEventService service) {
        this.service = service;
    }
}
