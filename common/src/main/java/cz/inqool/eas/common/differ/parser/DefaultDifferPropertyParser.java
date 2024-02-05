package cz.inqool.eas.common.differ.parser;

import cz.inqool.eas.common.differ.annotation.DiffedProperty;
import cz.inqool.eas.common.differ.exception.DifferException;
import cz.inqool.eas.common.differ.model.DiffedType;
import cz.inqool.eas.common.differ.model.prop.DifferAttribute;
import cz.inqool.eas.common.differ.model.prop.DifferMethod;
import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import cz.inqool.eas.common.differ.strategy.ComparingStrategy;
import cz.inqool.eas.common.domain.Domain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;

import javax.persistence.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.differ.util.DifferUtils.*;
import static cz.inqool.eas.common.utils.ReflectionUtils.*;

// beaned in DifferConfiguration
@Slf4j
public class DefaultDifferPropertyParser implements DifferPropertyParser {

    private Map<DiffedType, ComparingStrategy> strategies;


    /**
     * Translates java's attribute into DifferProperty.
     *
     * Structured attribute (e.g. embedded class instance) will be parsed into the depth,
     * by attaching subfields to the topmost structured instance.
     *
     * @param property       attribute of entity that shall be diffed
     * @param residencyClazz class containing diffed attribute
     * @return parsed field that shall be used by the differ-module
     */
    @Override
    public DifferProperty<?> parse(AccessibleObject property, Class<?> residencyClazz) {
        if (property instanceof Field) {
            return parseAttribute((Field) property, residencyClazz, null);
        }

        if (property instanceof Method) {
            return parseMethod((Method) property, residencyClazz, null);
        }

        throw new DifferException("Only Field or Method are allowed properties")
                .debugInfo(info -> info
                        .property("unsupportedProperty", property)
                        .property("residencyClass", residencyClazz.getSimpleName())
                );
    }


    private DifferProperty<Field> parseAttribute(Field attribute, Class<?> residencyClazz, DifferProperty<?> parent) {
        String propertyName = propertyName(attribute.getAnnotation(DiffedProperty.class), attribute.getName());

        // Embedded class - parse RECURSIVELY
        // It will parse ALL properties (@DiffedProperty or @DiffedIgnore is not taken into account)
        // to override this, create your own implementation of DifferPropertyParser
        if (attribute.isAnnotationPresent(Embedded.class)) {
            ComparingStrategy strategy = getComparingStrategy(DiffedType.EMBEDDED);

            DifferProperty<Field> embeddedField = new DifferAttribute(propertyName, attribute, residencyClazz, strategy, parent);

            Class<?> embeddedClazz = ResolvableType.forField(attribute).resolve();
            if (embeddedClazz == null) {
                // should not happen
                throw new DifferException("Could not resolve type for property")
                        .debugInfo(info -> info
                                .property("propertyName", attribute.getName())
                                .property("residencyClass", residencyClazz.getSimpleName())
                        );
            }

            // recursively create sub properties of embedded field - fields and methods

            for (Field innerAttribute : FieldUtils.getAllFields(embeddedClazz)) {
                // field is an attribute (and not a constant or something else)
                if (fieldIsAttribute(innerAttribute)) {
                    DifferProperty<Field> innerField = parseAttribute(innerAttribute, embeddedClazz, embeddedField);

                    embeddedField.getSubProperties().add(innerField);
                }
            }

            for (Method method : embeddedClazz.getMethods()) {
                // method is a getter
                if (methodNameStartsAsGetter(method)
                    && methodHasGetterDeclaration(method)
                    && methodDeclaringClassIsNotObject(method)) {

                    DifferProperty<Method> innerMethod = parseMethod(method, embeddedClazz, embeddedField);

                    embeddedField.getSubProperties().add(innerMethod);
                }
            }

            return embeddedField;
        }

        // To-Many association (collection associations)
        else if (attribute.isAnnotationPresent(OneToMany.class) || attribute.isAnnotationPresent(ManyToMany.class)) {
            ComparingStrategy strategy = getComparingStrategy(DiffedType.ENTITY_COLLECTION);

            return new DifferAttribute(propertyName, attribute, residencyClazz, strategy, parent);
        }
        // To-One association
        else if (attribute.isAnnotationPresent(OneToOne.class) || attribute.isAnnotationPresent(ManyToOne.class)) {
            ComparingStrategy strategy = getComparingStrategy(DiffedType.ENTITY);

            return new DifferAttribute(propertyName, attribute, residencyClazz, strategy, parent);
        }

        // Regular field such as String, Integer, int, boolean, Instant, LocalDate + Enums (or collection of it)
        ComparingStrategy strategy = isCollection(resolveType(attribute))
                ? getComparingStrategy(DiffedType.SIMPLE_COLLECTION)
                : getComparingStrategy(DiffedType.SIMPLE);

        return new DifferAttribute(propertyName, attribute, residencyClazz, strategy, parent);
    }

    private DifferProperty<Method> parseMethod(Method method, Class<?> residencyClazz, DifferProperty<?> parent) {
        String propertyName = propertyName(method.getAnnotation(DiffedProperty.class), methodNameToProperty(method));

        ComparingStrategy strategy;
        // check if method's return type is collection
        Class<?> methodReturnType = method.getReturnType();
        if (isCollection(methodReturnType)) {
            if (doesTypeImplementInterface(resolveType(method), Domain.class)) {
                // collection of entities
                strategy = getComparingStrategy(DiffedType.ENTITY_COLLECTION);
            } else {
                // if collection is not for entities, then deduce it is a simple collection
                // fixme: what of collection of embedds?
                strategy = getComparingStrategy(DiffedType.SIMPLE_COLLECTION);
            }
        } else { // return type is not method
            if (doesTypeImplementInterface(resolveType(method), Domain.class)) {
                // is it an Entity?
                strategy = getComparingStrategy(DiffedType.ENTITY);
            } else {
                // default is SIMPLE
                strategy = getComparingStrategy(DiffedType.SIMPLE);
            }
        }

        return new DifferMethod(propertyName, method, residencyClazz, strategy, parent);
    }

    private ComparingStrategy getComparingStrategy(DiffedType type) {
        return this.strategies.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(type))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new DifferException("No strategy for type " + type + " was registered")
                        .debugInfo(info -> info
                                .property("strategies", strategies)
                        ));
    }

    @Autowired
    public void setStrategies(List<ComparingStrategy> strategies) {
        Map<DiffedType, List<ComparingStrategy>> collect = strategies.stream().collect(Collectors.groupingBy(ComparingStrategy::getType));

        Map<DiffedType, ComparingStrategy> map = new HashMap<>();
        collect.forEach((type, comparingStrategies) -> {
            if (comparingStrategies.size() > 1) {
                throw new DifferException("Multiple comparing strategies found for type " +
                                          type + ": " + Arrays.toString(comparingStrategies.toArray()))
                        .debugInfo(info -> info
                                .property(type.name())
                                .property("strategies", Arrays.toString(comparingStrategies.toArray())));
            }

            map.put(type, comparingStrategies.get(0));
        });

        this.strategies = map;
    }
}
