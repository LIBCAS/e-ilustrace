package cz.inqool.eas.common.event;

import org.springframework.context.ApplicationEvent;

public class AfterTransactionCommitEvent<T extends ApplicationEvent> extends ApplicationEvent {
    private final T event;

    public AfterTransactionCommitEvent(Object source, T event) {
        super(source);
        this.event = event;
    }

    public T getEvent() {
        return event;
    }
}
