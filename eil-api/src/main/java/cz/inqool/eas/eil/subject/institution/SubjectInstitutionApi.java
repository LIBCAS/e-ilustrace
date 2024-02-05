package cz.inqool.eas.eil.subject.institution;

import cz.inqool.eas.common.dated.DatedApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subject Institution API", description = "Api for working with institutions that occur as a record (book or illustration) subject")
@RestController
@RequestMapping("/subject/institution")
public class SubjectInstitutionApi extends DatedApi<
        SubjectInstitution,
        SubjectInstitutionDetail,
        SubjectInstitutionList,
        SubjectInstitutionCreate,
        SubjectInstitutionUpdate,
        SubjectInstitutionService
        > {
}
