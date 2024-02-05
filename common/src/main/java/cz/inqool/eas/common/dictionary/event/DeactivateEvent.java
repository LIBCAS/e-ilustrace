package cz.inqool.eas.common.dictionary.event;

import cz.inqool.eas.common.domain.DomainService;
import org.springframework.context.PayloadApplicationEvent;

/**
 * Generic event fired when a existing object is deactivated.
 * @param <T> Type of object
 */
public class DeactivateEvent<T> extends PayloadApplicationEvent<T> {

    public DeactivateEvent(DomainService service, T payload) {
        super(service, payload);
    }
}
