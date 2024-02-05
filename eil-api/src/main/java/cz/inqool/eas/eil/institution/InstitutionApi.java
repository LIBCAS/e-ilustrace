package cz.inqool.eas.eil.institution;

import cz.inqool.eas.common.dated.DatedApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Institution API", description = "Api for working with institutions")
@RestController
@RequestMapping("/institution")
public class InstitutionApi extends DatedApi<
        Institution,
        InstitutionDetail,
        InstitutionList,
        InstitutionCreate,
        InstitutionUpdate,
        InstitutionService
        > {
}
