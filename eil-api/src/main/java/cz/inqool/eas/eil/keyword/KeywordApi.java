package cz.inqool.eas.eil.keyword;

import cz.inqool.eas.common.domain.DomainApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Keyword API", description = "Api for working with keywords")
@RestController
@RequestMapping("/keyword")
public class KeywordApi extends DomainApi<
        Keyword,
        KeywordDetail,
        KeywordList,
        KeywordCreate,
        KeywordUpdate,
        KeywordService> {
}

