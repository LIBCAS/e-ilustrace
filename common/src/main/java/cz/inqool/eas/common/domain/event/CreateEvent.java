package cz.inqool.eas.common.domain.event;

import cz.inqool.eas.common.domain.DomainService;
import org.springframework.context.PayloadApplicationEvent;

/**
 * Generic event fired when a new object is created.
 * @param <T> Type of object
 */
public class CreateEvent<T> extends PayloadApplicationEvent<T> {

    public CreateEvent(DomainService service, T payload) {
        super(service, payload);
    }
}
