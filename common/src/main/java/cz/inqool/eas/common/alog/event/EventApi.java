package cz.inqool.eas.common.alog.event;

import cz.inqool.eas.common.authored.AuthoredApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Alog events", description = "Event CRUD API")
@ResponseBody
@RequestMapping("${alog.url}")
public class EventApi extends AuthoredApi<
        Event,
        EventDetail,
        EventList,
        EventCreate,
        EventUpdate,
        EventService> {
}
