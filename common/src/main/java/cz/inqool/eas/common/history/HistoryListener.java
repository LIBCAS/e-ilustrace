package cz.inqool.eas.common.history;

import cz.inqool.eas.common.dictionary.event.ActivateEvent;
import cz.inqool.eas.common.dictionary.event.DeactivateEvent;
import cz.inqool.eas.common.domain.event.CreateEvent;
import cz.inqool.eas.common.domain.event.DeleteEvent;
import cz.inqool.eas.common.domain.event.UpdateEvent;
import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.common.history.operation.CommonHistoryOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;

@Slf4j
public class HistoryListener {
    private HistoryService service;

    @EventListener
    public void handleCreate(CreateEvent<?> event) {
        handleEvent(CommonHistoryOperation.CREATE, event);
    }

    @EventListener
    public void handleUpdate(UpdateEvent<?> event) {
        handleEvent(CommonHistoryOperation.UPDATE, event);
    }

    @EventListener
    public void handleDelete(DeleteEvent<?> event) {
        handleEvent(CommonHistoryOperation.DELETE, event);
    }

    @EventListener
    public void handleActivate(ActivateEvent<?> event) {
        handleEvent(CommonHistoryOperation.ACTIVATE, event);
    }

    @EventListener
    public void handleDeactivate(DeactivateEvent<?> event) {
        handleEvent(CommonHistoryOperation.DEACTIVATE, event);
    }

    protected void handleEvent(CommonHistoryOperation operation, PayloadApplicationEvent<?> event) {
        Object payload = event.getPayload();

        if (payload instanceof History) {
            return;
        }

        if (payload instanceof DomainObject) {
            DomainObject domainObject = (DomainObject) payload;

            History history = new History();
            history.setEntityId(domainObject.getId());
            history.setOperation(operation.toReference());

            service.createInternal(history);
        }
    }

    @Autowired
    public void setService(HistoryService service) {
        this.service = service;
    }
}
