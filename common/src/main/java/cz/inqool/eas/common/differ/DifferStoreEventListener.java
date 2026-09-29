package cz.inqool.eas.common.differ;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.event.store.*;
import cz.inqool.eas.common.domain.store.DomainStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;

import static cz.inqool.eas.common.differ.event.DifferActionType.*;

// beaned in DifferConfiguration
@Slf4j
public class DifferStoreEventListener {

    private DifferModule differModule;

    // ---------------- CREATE ----------------
    @EventListener
    @SuppressWarnings("unchecked")
    public <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> void listen(StorePreCreateEvent<ROOT, PROJECTED> event) {
        DomainStore<ROOT, PROJECTED, ?> store = event.getStore();

        differModule.differentiate(
                () -> null,
                event::getNewEntity,
                (Class<PROJECTED>) store.getType(),
                store.getRootType(),
                BEFORE_CREATE
        );
    }

    @EventListener
    @SuppressWarnings("unchecked")
    public <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> void listen(StorePostCreateEvent<ROOT, PROJECTED> event) {
        DomainStore<ROOT, PROJECTED, ?> store = event.getStore();

        differModule.differentiate(
                () -> null,
                event::getNewEntity,
                (Class<PROJECTED>) store.getType(),
                store.getRootType(),
                AFTER_CREATE
        );
    }

    @EventListener
    public <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> void listen(StorePreCreateCollectionEvent<ROOT, PROJECTED> event) {
        DomainStore<ROOT, PROJECTED, ?> store = event.getStore();

        differModule.differentiateCollection(
                ArrayList::new,
                event::getNewEntities,
                store.getRootType(),
                BEFORE_CREATE
        );
    }

    @EventListener
    public <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> void listen(StorePostCreateCollectionEvent<ROOT, PROJECTED> event) {
        DomainStore<ROOT, PROJECTED, ?> store = event.getStore();

        differModule.differentiateCollection(
                ArrayList::new,
                event::getNewEntities,
                store.getRootType(),
                AFTER_CREATE
        );
    }

    // ---------------- UPDATE ----------------

    @EventListener
    @SuppressWarnings("unchecked")
    public <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> void listen(StorePreUpdateEvent<ROOT, PROJECTED> event) {
        DomainStore<ROOT, PROJECTED, ?> store = event.getStore();

        differModule.differentiate(
                event::getOldEntity,
                event::getNewEntity,
                (Class<PROJECTED>) store.getType(),
                store.getRootType(),
                BEFORE_UPDATE
        );
    }

    @EventListener
    @SuppressWarnings("unchecked")
    public <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> void listen(StorePostUpdateEvent<ROOT, PROJECTED> event) {
        DomainStore<ROOT, PROJECTED, ?> store = event.getStore();

        differModule.differentiate(
                event::getOldEntity,
                event::getNewEntity,
                (Class<PROJECTED>) store.getType(),
                store.getRootType(),
                AFTER_UPDATE
        );
    }

    @EventListener
    public <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> void listen(StorePreUpdateCollectionEvent<ROOT, PROJECTED> event) {
        DomainStore<ROOT, PROJECTED, ?> store = event.getStore();

        differModule.differentiateCollection(
                event::getOldEntities,
                event::getNewEntities,
                store.getRootType(),
                BEFORE_UPDATE
        );
    }

    @EventListener
    public <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> void listen(StorePostUpdateCollectionEvent<ROOT, PROJECTED> event) {
        DomainStore<ROOT, PROJECTED, ?> store = event.getStore();

        differModule.differentiateCollection(
                event::getOldEntities,
                event::getNewEntities,
                store.getRootType(),
                AFTER_UPDATE
        );
    }

    @Autowired
    public void setDifferModule(DifferModule differModule) {
        this.differModule = differModule;
    }
}
