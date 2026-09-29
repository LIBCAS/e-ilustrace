package cz.inqool.eas.common.dictionary.event;

import cz.inqool.eas.common.domain.DomainService;
import org.springframework.context.PayloadApplicationEvent;

/**
 * Generic event fired when a existing object is activated.
 * @param <T> Type of object
 */
public class ActivateEvent<T> extends PayloadApplicationEvent<T> {

    public ActivateEvent(DomainService service, T payload) {
        super(service, payload);
    }
}
