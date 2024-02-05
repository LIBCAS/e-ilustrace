package cz.inqool.eas.eil.author;

import cz.inqool.eas.common.dated.DatedApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Author API", description = "Api for working with authors")
@RestController
@RequestMapping("/author")
public class AuthorApi extends DatedApi<
        Author,
        AuthorDetail,
        AuthorList,
        AuthorCreate,
        AuthorUpdate,
        AuthorService
        > {
}
