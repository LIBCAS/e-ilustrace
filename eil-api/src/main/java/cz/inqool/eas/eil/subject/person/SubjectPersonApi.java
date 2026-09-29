package cz.inqool.eas.eil.subject.person;

import cz.inqool.eas.common.dated.DatedApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subject person API", description = "Api for working with persons that occur as a record (book or illustration) subject")
@RestController
@RequestMapping("/subject/person")
public class SubjectPersonApi extends DatedApi<
        SubjectPerson,
        SubjectPersonDetail,
        SubjectPersonList,
        SubjectPersonCreate,
        SubjectPersonUpdate,
        SubjectPersonService
        > {
}
