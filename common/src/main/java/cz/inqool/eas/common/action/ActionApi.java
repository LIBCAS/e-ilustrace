package cz.inqool.eas.common.action;

import cz.inqool.eas.common.dictionary.DictionaryApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Backend actions", description = "Backend actions CRUD API")
@ResponseBody
@RequestMapping("${action.url}")
public class ActionApi extends DictionaryApi<
        Action,
        ActionDetail,
        ActionList,
        ActionCreate,
        ActionUpdate,
        ActionService> {
}
