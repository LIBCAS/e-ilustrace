package cz.inqool.eas.eil.subject.place;

import cz.inqool.eas.common.dated.DatedApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subject place API", description = "Api for working with places that occur as a record (book or illustration) subject")
@RestController
@RequestMapping("/subject/place")
public class SubjectPlaceApi extends DatedApi<
        SubjectPlace,
        SubjectPlaceDetail,
        SubjectPlaceList,
        SubjectPlaceCreate,
        SubjectPlaceUpdate,
        SubjectPlaceService
        > {
}
