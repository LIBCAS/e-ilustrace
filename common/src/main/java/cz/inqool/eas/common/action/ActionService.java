package cz.inqool.eas.common.action;

import cz.inqool.eas.common.dictionary.DictionaryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionService extends DictionaryService<
        Action,
        ActionDetail,
        ActionList,
        ActionCreate,
        ActionUpdate,
        ActionRepository
        > {
}
