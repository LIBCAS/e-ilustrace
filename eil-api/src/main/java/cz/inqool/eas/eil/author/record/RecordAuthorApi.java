package cz.inqool.eas.eil.author.record;

import cz.inqool.eas.common.domain.DomainApi;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.eil.author.AuthorList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;

@Tag(name = "RecordAuthor API", description = "Api for working with record authors")
@RestController
@RequestMapping("/record-author")
public class RecordAuthorApi extends DomainApi<
        RecordAuthor,
        RecordAuthorDetail,
        RecordAuthorList,
        RecordAuthorCreate,
        RecordAuthorUpdate,
        RecordAuthorService
        > {

    @Operation(summary = "List authors satisfying given params")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/list-authors")
    public Set<AuthorList> listAuthors(@Valid @RequestBody(required = false) Params params) {
        return service.listAuthors(params);
    }
}
