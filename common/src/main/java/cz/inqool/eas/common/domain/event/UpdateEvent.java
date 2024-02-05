package cz.inqool.eas.common.domain.event;

import cz.inqool.eas.common.domain.DomainService;
import org.springframework.context.PayloadApplicationEvent;

/**
 * Generic event fired when a existing object is updated.
 * @param <T> Type of object
 */
public class UpdateEvent<T> extends PayloadApplicationEvent<T> {

    public UpdateEvent(DomainService service, T payload) {
        super(service, payload);
    }
}
