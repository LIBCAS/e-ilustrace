package cz.inqool.eas.eil.subject.entry;

import cz.inqool.eas.common.dated.DatedApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subject entry API", description = "Api for working with subject entries")
@RestController
@RequestMapping("/subject/entry")
public class SubjectEntryApi extends DatedApi<
        SubjectEntry,
        SubjectEntryDetail,
        SubjectEntryList,
        SubjectEntryCreate,
        SubjectEntryUpdate,
        SubjectEntryService
        > {
}
