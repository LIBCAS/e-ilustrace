package cz.inqool.eas.eil.genre;

import cz.inqool.eas.common.dated.DatedApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Genre (form) API", description = "Api for working with genres (forms)")
@RestController
@RequestMapping("/genre")
public class GenreApi extends DatedApi<
        Genre,
        GenreDetail,
        GenreList,
        GenreCreate,
        GenreUpdate,
        GenreService
        > {
}
