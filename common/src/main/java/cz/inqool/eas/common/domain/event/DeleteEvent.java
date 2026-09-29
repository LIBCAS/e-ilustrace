package cz.inqool.eas.common.domain.event;

import cz.inqool.eas.common.domain.DomainService;
import org.springframework.context.PayloadApplicationEvent;

/**
 * Generic event fired when a existing object is deleted.
 * @param <T> Type of object
 */
public class DeleteEvent<T> extends PayloadApplicationEvent<T> {

    public DeleteEvent(DomainService service, T payload) {
        super(service, payload);
    }
}
