package cz.inqool.eas.common.differ;

import cz.inqool.eas.common.differ.event.DifferActionType;
import cz.inqool.eas.common.differ.event.DifferCollectionActionEvent;
import cz.inqool.eas.common.differ.event.DifferSingleActionEvent;
import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.domain.Domain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

/**
 * Notifier - Listeners is basically Observer pattern / Publish-Subscribe
 *
 * fixme: eventPublisher.publish bude skor Notifier nez tato classa, este domysliet
 *
 * https://refactoring.guru/design-patterns/observer/java/example
 *
 * TODO: Look into Hibernate DifferEventPostUpdateListener, PostUpdateEventListener
 */
// beaned in DifferConfiguration
@Slf4j
public class DifferEventNotifier {

    private ApplicationEventPublisher eventPublisher;

    /**
     * Publish differ event for SINGLE create/update
     *
     * @param rootType root type of repository that diffed entities (which repository.create/update was called)
     * @param entity   new object
     * @param changes  map of changes for object
     * @param action   what action invoked differentiation
     */
    public void publishEvent(
            Class<? extends Domain<?>> rootType,
            Domain<?> entity,
            Map<String, DifferChange> changes,
            DifferActionType action
    ) {
        eventPublisher.publishEvent(new DifferSingleActionEvent<>(this, rootType, action, entity, changes));
    }

    /**
     * Publish differ event for COLLECTION create/update
     *
     * @param rootType root type of repository that diffed entities (which repository.create/update was called)
     * @param changes  map of changes
     * @param action   what action invoked differentiation
     */
    public void publishEvent(
            Class<? extends Domain<?>> rootType,
            Map<Domain<?>, Map<String, DifferChange>> changes,
            DifferActionType action
    ) {
        eventPublisher.publishEvent(new DifferCollectionActionEvent<>(this, rootType, action, changes));
    }


    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

}
