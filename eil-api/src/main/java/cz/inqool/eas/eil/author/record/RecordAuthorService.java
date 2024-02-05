package cz.inqool.eas.eil.author.record;

import cz.inqool.eas.common.domain.DomainService;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.eil.author.AuthorList;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;

@Service
public class RecordAuthorService extends DomainService<
        RecordAuthor,
        RecordAuthorDetail,
        RecordAuthorList,
        RecordAuthorCreate,
        RecordAuthorUpdate,
        RecordAuthorRepository
        > {

    public Set<AuthorList> listAuthors(Params params) {
        params = coalesce(params, Params::new);

        Result<RecordAuthorList> recordAuthors = list(params);
        return recordAuthors.getItems().stream().map(RecordAuthorList::getAuthor).collect(Collectors.toSet());
    }
}
