package cz.inqool.eas.eil.publishingplace;

import cz.inqool.eas.common.dated.DatedApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Publishing place API", description = "Api for working with places of publishing")
@RestController
@RequestMapping("/publishing-place")
public class PublishingPlaceApi extends DatedApi<
        PublishingPlace,
        PublishingPlaceDetail,
        PublishingPlaceList,
        PublishingPlaceCreate,
        PublishingPlaceUpdate,
        PublishingPlaceService
        > {
}
