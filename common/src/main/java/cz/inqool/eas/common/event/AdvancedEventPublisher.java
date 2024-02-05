package cz.inqool.eas.common.event;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Extended event publisher providing method for publishing event after the transaction commit.
 */
@Service
public class AdvancedEventPublisher implements ApplicationEventPublisher {
    protected ApplicationEventPublisher eventPublisher;

    /**
     * Delegates to the real eventPublisher.
     */
    @Override
    public void publishEvent(@NotNull ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }

    /**
     * Delegates to the real eventPublisher.
     */
    @Override
    public void publishEvent(@NotNull Object event) {
        eventPublisher.publishEvent(event);
    }

    /**
     * Publishes intermediate AfterTransactionCommitEvent, which will be handled after transaction commit
     * and only after that, the real event is thrown.
     */
    public void publishAfterTransactionCommitEvent(@NotNull ApplicationEvent event) {
        eventPublisher.publishEvent(new AfterTransactionCommitEvent<>(event.getSource(), event));
    }

    /**
     * Handles intermediate AfterTransactionCommitEvent event and emit the real event after transaction is commited.
     */
    @TransactionalEventListener
    public void handleAfterTransactionCommitEvent(AfterTransactionCommitEvent event) {
        publishEvent(event.getEvent());
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
