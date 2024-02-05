package cz.inqool.eas.common.alog.event;

import cz.inqool.eas.common.alog.AlogAccessChecker;
import cz.inqool.eas.common.authored.AuthoredService;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.ForbiddenObject;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.module.ModuleReference;
import cz.inqool.eas.common.module.Modules;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.OBJECT_ACCESS_DENIED;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.OPERATION_ACCESS_DENIED;
import static cz.inqool.eas.common.utils.AssertionUtils.isTrue;

/**
 * CRUD service for audit log events.
 */
public class EventService extends AuthoredService<
        Event,
        EventDetail,
        EventList,
        EventCreate,
        EventUpdate,
        EventRepository
        > {

    private AlogAccessChecker accessChecker;

    @Setter
    private String source;

    @Override
    protected void preCreateHook(@NotNull Event event) {
        super.preCreateHook(event);
        event.setSource(source);

        if (event.getModule() == null) {
            event.setModule(ModuleReference.of(Modules.EAS));
        }
    }

    @Override
    protected void postGetHook(Event object) {
        super.postGetHook(object);

        isTrue(accessChecker.checkRecordAccess(object), () -> new ForbiddenObject(OBJECT_ACCESS_DENIED)
                .details(details -> details.id(object.getId()).property("type", Event.class.getSimpleName()))
                .debugInfo(info -> info.clazz(Event.class)));
    }

    @Override
    protected void preListHook(Params params) {
        super.preListHook(params);

        isTrue(accessChecker.checkListAccess(), () -> new ForbiddenOperation(OPERATION_ACCESS_DENIED)
                .details(details -> details.property("type", Event.class.getSimpleName()))
                .debugInfo(info -> info.clazz(Event.class)));
    }

    @Autowired
    public void setAccessChecker(AlogAccessChecker accessChecker) {
        this.accessChecker = accessChecker;
    }
}
