package cz.inqool.eas.common.differ;

import cz.inqool.eas.common.differ.event.DifferActionType;
import cz.inqool.eas.common.differ.exception.DifferException;
import cz.inqool.eas.common.differ.model.DifferChange;
import cz.inqool.eas.common.differ.model.DifferPropertiesMap;
import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import cz.inqool.eas.common.differ.util.PairNullable;
import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

// beaned in DifferConfiguration
@Slf4j
public class DifferModule {
    private DifferEventNotifier eventNotifier;
    private DifferFieldsProcessor fieldsProcessor;

    /**
     * Thread safe impl, because multiple api-request threads might access this map and edit it.
     * Because mappings are computed on demand for View Stores (as they are created on demand).
     */
    private final ConcurrentNavigableMap<Class<? extends Domain<?>>, DifferPropertiesMap> differMap
            = new ConcurrentSkipListMap<>(Comparator.comparing(Class::getName));


    /**
     * Differentiate two objects of the same type and publish event.
     */
    public <TYPE extends Domain<?>> void differentiate(
            @NonNull Supplier<TYPE> oldObjectGetter,
            @NonNull Supplier<TYPE> newObjectGetter,
            @NonNull Class<TYPE> realType,
            @NonNull Class<? extends Domain<?>> rootType,
            @NonNull DifferActionType action
    ) {
        if (DifferModuleState.isDisabled()) {
            // skip if module diffing is disabled
            return;
        }

        boolean shouldDifferentiate = this.isTypeDifferentiable(realType);
        if (!shouldDifferentiate) {
            // type is not differentiable
            return;
        }

        TYPE oldObject = oldObjectGetter.get();
        TYPE newObject = newObjectGetter.get();

        var changes = this.computeChanges(realType, oldObject, newObject);

        eventNotifier.publishEvent(rootType, newObject, changes, action);
    }

    /**
     * Differentiate two collection of objects of the same type  and publish event.
     *
     * Diffing is done on two entities of the same IDs by pairing them.
     */
    public <TYPE extends Domain<?>> void differentiateCollection(
            @NonNull Supplier<List<TYPE>> oldObjectsProvider,
            @NonNull Supplier<List<TYPE>> newObjectsGetter,
            @NonNull Class<? extends Domain<?>> rootType,
            @NonNull DifferActionType action
    ) {
        if (DifferModuleState.isDisabled()) {
            // skip if module diffing is disabled
            return;
        }

        Collection<TYPE> allNewObjects = newObjectsGetter.get();

        // Collection create/update requires bigger handling than single action
        // because collection does not necessarily consist of same type
        // (I can have a Collection<A>, where A is superclass and real classes of collection elements are B and C.
        // Class B might be differentiable but Class C is not

        // group new objects by its type
        Map<Class<TYPE>, List<TYPE>> newObjectsGroups = allNewObjects.stream().collect(Collectors.groupingBy(o -> (Class<TYPE>) o.getClass()));

        List<PairNullable<TYPE, TYPE>> allObjectPairs = new ArrayList<>();
        for (var newObjectsGroup : newObjectsGroups.entrySet()) { // newObjects of same class
            Class<TYPE> realType = newObjectsGroup.getKey();
            boolean shouldDifferentiate = this.isTypeDifferentiable(realType);
            if (!shouldDifferentiate) {
                // type is not differentiable -> skip this group
                continue;
            }

            List<TYPE> newObjects = Collections.unmodifiableList(newObjectsGroup.getValue());
            List<TYPE> oldObjects = Collections.unmodifiableList(oldObjectsProvider.get());
            List<PairNullable<TYPE, TYPE>> objectPairs = preparePairs(oldObjects, newObjects);
            allObjectPairs.addAll(objectPairs);
        }

        // NewObject : Map of differ changes
        Map<Domain<?>, Map<String, DifferChange>> changesMap = allObjectPairs.stream()
                .collect(Collectors.toMap(
                        PairNullable::getNewObject,
                        pair -> {
                            TYPE oldObject = pair.getOldObject();
                            TYPE newObject = pair.getNewObject();
                            Class<TYPE> realType = (Class<TYPE>) newObject.getClass();
                            // compute changes for entity pair
                            var changes = this.computeChanges(realType, oldObject, newObject);

                            return changes;
                        }
                ));

        eventNotifier.publishEvent(rootType, changesMap, action);
    }

    @NotNull
    private <TYPE extends Domain<?>> List<String> getIds(List<TYPE> newObjects) {
        return newObjects.stream().map(Domain::getId).collect(Collectors.toUnmodifiableList());
    }

    private <TYPE extends Domain<?>> Map<String, DifferChange> computeChanges(Class<TYPE> realType, @Nullable TYPE oldObject, @NonNull TYPE newObject) {
        DifferPropertiesMap differPropertiesMap = differMap.get(realType);
        notNull(differPropertiesMap, () -> new DifferException("Attempting to detect changes for entity class '" + realType.getSimpleName() + "' for which there is no parsed differ fields map!")
                .debugInfo(info -> info.clazz(realType)));

        // changes of subfields shall be computed by the top-most parents
        // upon merge conflict overwrite with new value
        // sort by keys
        TreeMap<String, DifferChange> map = new TreeMap<>();
        for (DifferProperty<?> differField : differPropertiesMap.values()) {
            if (differField.isNotSubProperty()) {
                Set<DifferChange> differChanges = differField.diffObjects(oldObject, newObject);
                for (DifferChange differChange : differChanges) {
                    map.put(differChange.getAncestryPath(), differChange);
                }
            }
        }
        return map;
    }

    public boolean isTypeDifferentiable(Class<? extends Domain<?>> type) {
        DifferPropertiesMap differPropertiesMap = differMap.get(type);

        // return true if type has at least one parsed differ field
        return differPropertiesMap != null && differPropertiesMap.size() > 0;
    }

    /**
     * Analyzes differentiable fields of this repository's entity.
     *
     * ! type can be abstract class that has subclasses !
     * ! type can be an entity view !
     */
    public <TYPE extends Domain<?>> void processClass(Class<TYPE> typeClass) {
        processClass(typeClass, true);
    }

    /**
     * FIXME: For testing purposes; to enable reprocessing via api call
     */
    public <TYPE extends Domain<?>> void processClass(Class<TYPE> typeClass, boolean skipIfPresent) {
        if (differMap.containsKey(typeClass) && skipIfPresent) {
            // skip if already processed
            return;
        }

        // process given class
        processDifferentiableEntity(typeClass);

        // process subclasses
        for (var subClass : ReflectionUtils.getSubTypesOf(typeClass)) {
            processDifferentiableEntity(subClass);
        }
    }

    /**
     * List all available mappings.
     *
     * @return Map {Class name} : {ancestry paths to fields}
     */
    public Map<String, List<String>> getMappings() {
        return this.differMap.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().keySet().isEmpty())
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getSimpleName(),
                        entry -> new ArrayList<>(entry.getValue().keySet())
                ));
    }

    private void processDifferentiableEntity(Class<? extends Domain<?>> typeClass) {
        DifferPropertiesMap parsedMap = fieldsProcessor.process(typeClass);
        differMap.put(typeClass, parsedMap);
    }

    private <TYPE extends Domain<?>> List<PairNullable<TYPE, TYPE>> preparePairs(@NonNull List<TYPE> oldObjects, @NonNull List<TYPE> newObjects) {
        Map<String, TYPE> oldObjectsMap = oldObjects.stream().collect(Collectors.toMap(Domain::getId, Function.identity()));

        return newObjects.stream()
                .map(newObject -> {
                    // default value is null to indicate that old object was NOT present -> meaning new object was created
                    TYPE oldObject = oldObjectsMap.getOrDefault(newObject.getId(), null);
                    return PairNullable.of(oldObject, newObject);
                })
                .collect(Collectors.toUnmodifiableList());
    }


    @Autowired
    public void setEventNotifier(DifferEventNotifier eventNotifier) {
        this.eventNotifier = eventNotifier;
    }

    @Autowired
    public void setFieldsProcessor(DifferFieldsProcessor fieldsProcessor) {
        this.fieldsProcessor = fieldsProcessor;
    }
}
