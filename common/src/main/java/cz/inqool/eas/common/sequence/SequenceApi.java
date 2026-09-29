package cz.inqool.eas.common.sequence;

import cz.inqool.eas.common.dictionary.DictionaryApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Sequences", description = "Sequences CRUD API")
@ResponseBody
@RequestMapping("${sequence.url}")
public class SequenceApi extends DictionaryApi<
        Sequence,
        SequenceDetail,
        SequenceList,
        SequenceCreate,
        SequenceUpdate,
        SequenceService> {
}
