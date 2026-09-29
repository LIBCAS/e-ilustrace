package cz.inqool.eas.common.alog.event;

import cz.inqool.eas.common.authored.AuthoredRepository;
import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import static cz.inqool.eas.common.module.Modules.AUDIT_LOG;

/**
 * CRUD repository for audit log events.
 */
public class EventRepository extends AuthoredRepository<
        Event,
        Event,
        EventIndexedObject,
        AuthoredStore<Event, Event, QEvent>,
        AuthoredIndex<Event, Event, EventIndexedObject>> {
    @Override
    protected ModuleDefinition getModule() {
        return AUDIT_LOG;
    }
}
