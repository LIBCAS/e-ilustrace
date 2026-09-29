package cz.inqool.eas.common.domain;

import cz.inqool.eas.common.domain.event.CreateEvent;
import cz.inqool.eas.common.domain.event.DeleteEvent;
import cz.inqool.eas.common.domain.event.UpdateEvent;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.event.AdvancedEventPublisher;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.projection.Projectable;
import cz.inqool.eas.common.projection.Projection;
import cz.inqool.eas.common.projection.ProjectionFactory;
import net.jodah.typetools.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

/**
 * CRUD Service layer for objects implementing {@link Domain}.
 *
 * @param <ROOT>              Root of the projection type system
 * @param <DETAIL_PROJECTION> Projection used for retrieving object detail
 * @param <LIST_PROJECTION>   Projection used for listing object
 * @param <CREATE_PROJECTION> Projection used as DTO for object creation
 * @param <UPDATE_PROJECTION> Projection used as DTO for object update
 * @param <REPOSITORY>        Repository type
 */
public abstract class DomainService<
        ROOT extends Domain<ROOT>,
        DETAIL_PROJECTION extends Domain<ROOT>,
        LIST_PROJECTION extends Domain<ROOT>,
        CREATE_PROJECTION extends Projectable<ROOT>,
        UPDATE_PROJECTION extends Projectable<ROOT>,
        REPOSITORY extends DomainRepository<ROOT, ?, ?, ?, ?>> {
    protected Class<ROOT> rootType;
    protected Class<DETAIL_PROJECTION> detailType;
    protected Class<LIST_PROJECTION> listType;
    protected Class<CREATE_PROJECTION> createType;
    protected Class<UPDATE_PROJECTION> updateType;
    protected Projection<ROOT, ROOT, CREATE_PROJECTION> createProjection;
    protected Projection<ROOT, ROOT, UPDATE_PROJECTION> updateProjection;
    protected Projection<ROOT, ROOT, DETAIL_PROJECTION> detailProjection;

    protected ProjectionFactory projectionFactory;
    protected AdvancedEventPublisher eventPublisher;
    protected REPOSITORY repository;

    protected PlatformTransactionManager transactionManager;

    protected SimpMessagingTemplate webSocketTemplate;

    /**
     * Initializes types, projections and inject repository bean.
     *
     * Should be overrided only in case the types are not derivable from type arguments.
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    protected void init() {
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(DomainService.class, getClass());

        this.rootType = (Class<ROOT>) typeArguments[0];
        this.detailType = (Class<DETAIL_PROJECTION>) typeArguments[1];
        this.listType = (Class<LIST_PROJECTION>) typeArguments[2];
        this.createType = (Class<CREATE_PROJECTION>) typeArguments[3];
        this.updateType = (Class<UPDATE_PROJECTION>) typeArguments[4];

        this.createProjection = projectionFactory.get(rootType, createType);
        this.updateProjection = projectionFactory.get(rootType, updateType);
        this.detailProjection = projectionFactory.get(rootType, detailType);
    }

    /**
     * Creates object from projection.
     *
     * @param <PROJECTED> Type of projection
     * @param object      Object to create
     */
    @Transactional
    public <PROJECTED extends Domain<ROOT>> void createInternal(@NotNull PROJECTED object) {
        doWithRoot(object, this::preCreateHook);

        object = repository.create(object);

        doWithRoot(object, this::postCreateHook);

        eventPublisher.publishEvent(new CreateEvent<>(this, object));
    }

    /**
     * Updates object from projection
     *
     * @param <PROJECTED> Type of projection
     * @param object      Object to update
     */
    @Transactional
    public <PROJECTED extends Domain<ROOT>> void updateInternal(@NotNull PROJECTED object) {
        updateInternal(object, true);
    }

    /**
     * Updates object from projection
     *
     * @param <PROJECTED> Type of projection
     * @param object      Object to update
     */
    @Transactional
    public <PROJECTED extends Domain<ROOT>> void updateInternal(@NotNull PROJECTED object, boolean publishEvents) {
        doWithRoot(object, this::preUpdateHook);

        PROJECTED projected = repository.update(object);

        doWithRoot(projected, this::postUpdateHook);

        if (publishEvents) {
            eventPublisher.publishEvent(new UpdateEvent<>(this, projected));
        }
    }

    /**
     * Deletes object based on id and returns projection.
     *
     * @param id          Id of object to delete
     */
    @Transactional
    public void deleteInternal(@NotNull String id) {
        preDeleteHook(id);

        ROOT object = repository.delete(id);
        notNull(object, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                .debugInfo(info -> info.clazz(rootType))
                .logAll());

        postDeleteHook(object);
        eventPublisher.publishEvent(new DeleteEvent<>(this, object));
    }

    /**
     * Gets object based on id using projection.
     *
     * @param type        Projection type
     * @param id          Id of the object
     * @param <PROJECTED> Type of projection
     * @return The object
     */
    @Transactional
    public <PROJECTED extends Domain<ROOT>> PROJECTED getInternal(Class<PROJECTED> type, @NotNull String id) {
        preGetHook(id);

        PROJECTED view = repository.find(type, id);
        notNull(view, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", rootType.getSimpleName()))
                .debugInfo(info -> info.clazz(rootType))
                .logAll());

        view = doWithRoot(view, this::postGetHook);

        return view;
    }

    /**
     * Retrieves list view of objects that respect the selected {@link Params} using projection.
     *
     * @param type        Projection type
     * @param params      Parameters to comply with
     * @param <PROJECTED> Type of projection
     * @return Sorted list of objects with total number
     */
    @Transactional
    public <PROJECTED extends Domain<ROOT>> Result<PROJECTED> listInternal(Class<PROJECTED> type, @Nullable Params params) {
        params = coalesce(params, Params::new);

        preListHook(params);
        Result<PROJECTED> result = repository.listByParams(type, params);

        result = doWithRoot(result, this::postListHook);

        return result;
    }

    /**
     * Creates object based on create projection.
     *
     * @param view Provided view
     * @return Detail view of the created object
     */
    public DETAIL_PROJECTION create(CREATE_PROJECTION view) {
        TransactionTemplate template = new TransactionTemplate(this.transactionManager);

        ROOT object = createProjection.toBase(view);
        template.executeWithoutResult((status) -> createInternal(object));

        return template.execute((status) -> repository.find(detailType, object.getId()));
    }

    /**
     * Updates object based on update projection.
     *
     * @param view Provided view
     * @return Detail view of the updated object
     */
    public DETAIL_PROJECTION update(String id, UPDATE_PROJECTION view) {
        TransactionTemplate template = new TransactionTemplate(this.transactionManager);

        template.executeWithoutResult((status -> {
            ROOT root = repository.find(id);
            ROOT object = updateProjection.toBase(view, root);
            object.setId(id);

            updateInternal(object);
        }));

        return template.execute(status -> repository.find(detailType, id));
    }

    /**
     * Deletes object based on id and return detail projection.
     *
     * @param id Id of object to delete
     */
    @Transactional
    public void delete(String id) {
        deleteInternal(id);
    }

    /**
     * Gets detail projection of object based on id.
     *
     * @param id Id of the object
     * @return Detail view of the object
     */
    @Transactional
    public DETAIL_PROJECTION get(String id) {
        return this.getInternal(detailType, id);
    }


    /**
     * Retrieves list view of objects that respect the selected {@link Params} using list projection.
     *
     * @param params Parameters to comply with
     * @return Sorted list of objects with total number
     */
    @Transactional
    public Result<LIST_PROJECTION> list(@Nullable Params params) {
        return this.listInternal(listType, params);
    }

    public ROOT getRef(@NotNull String id) {
        return repository.getRef(id);
    }

    /**
     * Hook called before creating object.
     */
    protected void preCreateHook(@NotNull ROOT object) {
    }

    /**
     * Hook called after creating object.
     */
    protected void postCreateHook(@NotNull ROOT object) {
    }

    /**
     * Hook called before updating object.
     */
    protected void preUpdateHook(@NotNull ROOT object) {
    }

    /**
     * Hook called after updating object.
     *
     * Defaults to calling websocket if used.
     */
    protected void postUpdateHook(@NotNull ROOT object) {
        publishChangedWSEvent(object);
    }

    protected void publishChangedWSEvent(ROOT object) {
        if (webSocketTemplate != null) {
            webSocketTemplate.convertAndSend("/topic/entity/" + object.getId() + "/update", true);
        }
    }

    /**
     * Hook called before deleting object.
     */
    protected void preDeleteHook(@NotNull String id) {
    }

    /**
     * Hook called after deleting object.
     */
    protected void postDeleteHook(@NotNull ROOT object) {
    }

    /**
     * Hook called before getting object.
     */
    protected void preGetHook(@NotNull String id) {
    }

    /**
     * Hook called after getting object.
     */
    protected void postGetHook(@NotNull ROOT object) {
    }

    /**
     * Hook called before listing objects.
     */
    protected void preListHook(@NotNull Params params) {
    }

    @SuppressWarnings("unchecked")
    protected final <PROJECTED extends Domain<ROOT>> PROJECTED doWithRoot(PROJECTED object, Consumer<ROOT> consumer) {
        Class<PROJECTED> type = (Class<PROJECTED>) object.getClass();
        var projection = projectionFactory.get(rootType, type);

        ROOT root = projection.toBase(object);
        consumer.accept(root);
        return projection.toProjected(root);
    }

    @SuppressWarnings("unchecked")
    protected final <PROJECTED extends Domain<ROOT>> Result<PROJECTED> doWithRoot(Result<PROJECTED> result, Consumer<Result<ROOT>> consumer) {
        if (result.getItems().size() == 0) {
            return result;
        }

        Map<String, Class<PROJECTED>> projectedIdTypeMap = new HashMap<>();
        Map<Class<PROJECTED>, Projection<ROOT, ROOT, PROJECTED>> projections = new HashMap<>();

        List<ROOT> rootItems = result.
                getItems().
                stream().
                map(projected -> {
                    Class<PROJECTED> projectedType = (Class<PROJECTED>) projected.getClass();
                    Projection<ROOT, ROOT, PROJECTED> projection = projections.computeIfAbsent(projectedType,
                            type -> projectionFactory.get(rootType, type));
                    projectedIdTypeMap.put(projected.getId(), projectedType);
                    return projection.toBase(projected);
                }).
                collect(Collectors.toList());
        Result<ROOT> rootResult = new Result<>(rootItems, result.getCount(), result.getSearchAfter(), result.getAggregations());

        consumer.accept(rootResult);

        List<PROJECTED> projectedItems = rootResult.
                getItems().
                stream().
                map(base -> {
                    Class<PROJECTED> projectedType = projectedIdTypeMap.get(base.getId());
                    Projection<ROOT, ROOT, PROJECTED> projection = projections.get(projectedType);
                    return projection.toProjected(base);
                }).
                collect(Collectors.toList());
        return new Result<>(projectedItems, result.getCount(), result.getSearchAfter(), result.getAggregations());
    }

    /**
     * Hook called after listing object.
     */
    protected void postListHook(@NotNull Result<ROOT> result) {
    }

    @Autowired
    public void setProjectionFactory(ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
    }

    @Autowired
    public void setEventPublisher(AdvancedEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setRepository(REPOSITORY repository) {
        this.repository = repository;
    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Autowired(required = false)
    public void setWebSocketTemplate(SimpMessagingTemplate webSocketTemplate) {
        this.webSocketTemplate = webSocketTemplate;
    }
}
