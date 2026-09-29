package cz.inqool.eas.common.differ;

import cz.inqool.eas.common.differ.annotation.DiffedClass;
import cz.inqool.eas.common.differ.annotation.DiffedIgnore;
import cz.inqool.eas.common.differ.annotation.DiffedProperty;
import cz.inqool.eas.common.differ.model.DifferPropertiesMap;
import cz.inqool.eas.common.differ.model.prop.DifferProperty;
import cz.inqool.eas.common.differ.parser.DifferPropertyParser;
import cz.inqool.eas.common.domain.Domain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.differ.util.DifferUtils.*;
import static cz.inqool.eas.common.utils.CollectionUtils.intersect;
import static cz.inqool.eas.common.utils.CollectionUtils.isIntersect;

// beaned in DifferConfiguration
@Slf4j
public class DifferFieldsProcessor {

    private ApplicationContext applicationContext;

    /**
     * Parse class that is annotated by @DiffedClass into map containing fields
     *
     * @param residencyClazz entity class to be parsed
     * @see DiffedClass JavaDoc
     */
    public DifferPropertiesMap process(Class<? extends Domain<?>> residencyClazz) {
        // <field's ancestry path> : <DifferField>
        DifferPropertiesMap differentiableFieldsMap = new DifferPropertiesMap();

        // step 1 - 3  (see DiffedClass javadoc)
        processDiffedClass(differentiableFieldsMap, residencyClazz);
        // step 4
        processDiffedProperties(differentiableFieldsMap, residencyClazz);
        // step 5
        processDiffedIgnores(differentiableFieldsMap, residencyClazz);

        log.info("Differ parsing - {}: {}", residencyClazz.getSimpleName(), differentiableFieldsMap);

        return differentiableFieldsMap;
    }

    /**
     * Parse fields declared in class's {@link DiffedClass#properties}.
     */
    private void processDiffedClass(DifferPropertiesMap map, Class<? extends Domain<?>> residencyClazz) {
        DiffedClass annotation = residencyClazz.getAnnotation(DiffedClass.class);

        if (annotation == null) {
            // skip processing if entity class not annotated with @DiffedClass
            return;
        }

        DifferPropertiesMap temporarySet = new DifferPropertiesMap();

        // step 1 - process all properties
        ReflectionUtils.doWithFields(residencyClazz,
                field -> {
                    DifferPropertyParser differAttributeParser = getParserBean(annotation.parser());

                    DifferProperty<?> differField = differAttributeParser.parse(field, residencyClazz);
                    temporarySet.putField(differField, true);
                },
                field -> fieldIsAttribute(field)
        );

        ReflectionUtils.doWithMethods(residencyClazz,
                method -> {
                    DifferPropertyParser differAttributeParser = getParserBean(annotation.parser());

                    DifferProperty<?> differField = differAttributeParser.parse(method, residencyClazz);
                    // do not overwrite to avoid using method's parsing SIMPLE strategy
                    temporarySet.putField(differField, false);
                },
                method -> methodHasGetterDeclaration(method)
                          && methodNameStartsAsGetter(method)
                          && methodDeclaringClassIsNotObject(method)
        );

        // step 2 - retain only explicitly declared
        temporarySet.retainOnly(annotation.properties());

        // step 3 - remove if explicitly declared to be ignored
        temporarySet.removeAll(annotation.ignoreProperties());

        // log warning if there is an intersection of wanted + ignored properties
        if (isIntersect(annotation.properties(), annotation.ignoreProperties())) {
            Set<String> intersection = intersect(annotation.properties(), annotation.ignoreProperties());
            log.warn("Class {} with declared @DiffedClass has properties both wanted and ignored: {}", residencyClazz, Arrays.toString(intersection.toArray()));
        }

        map.mergeWith(temporarySet);
    }


    /**
     * Process fields and methods annotated with {@link DiffedProperty}.
     */
    private void processDiffedProperties(DifferPropertiesMap map, Class<? extends Domain<?>> residencyClazz) {
        // process attributes
        ReflectionUtils.doWithFields(residencyClazz,
                field -> {
                    DiffedProperty annotation = field.getAnnotation(DiffedProperty.class);

                    DifferPropertyParser differPropertyParser = getParserBean(annotation.parser());

                    DifferProperty<?> differField = differPropertyParser.parse(field, residencyClazz);
                    map.putField(differField, true);
                },
                field -> field.isAnnotationPresent(DiffedProperty.class)
                         && fieldIsAttribute(field)
        );

        // process methods
        ReflectionUtils.doWithMethods(residencyClazz,
                method -> {
                    DiffedProperty annotation = method.getAnnotation(DiffedProperty.class);

                    DifferPropertyParser differAttributeParser = getParserBean(annotation.parser());

                    DifferProperty<?> differField = differAttributeParser.parse(method, residencyClazz);
                    map.putField(differField, true);
                },
                method -> method.isAnnotationPresent(DiffedProperty.class)
                          && methodHasGetterDeclaration(method)
                          && methodDeclaringClassIsNotObject(method)
        );
    }

    /**
     * Process fields and methods annotated with {@link DiffedIgnore}.
     */
    private void processDiffedIgnores(DifferPropertiesMap map, Class<? extends Domain<?>> residencyClazz) {
        // process attributes
        ReflectionUtils.doWithFields(residencyClazz,
                field -> {
                    var fieldsToRemove = map.entrySet()
                            .stream()
                            // remove if differ field is parsed for given field
                            .filter(entry -> Objects.equals(field, entry.getValue().getJavaProperty()))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toSet());
                    fieldsToRemove.forEach(map::remove);
                },
                field -> field.isAnnotationPresent(DiffedIgnore.class)
        );

        // process methods
        ReflectionUtils.doWithMethods(residencyClazz,
                method -> {
                    var methodsToRemove = map.entrySet()
                            .stream()
                            // remove if differ field is parsed for given method
                            .filter(entry -> Objects.equals(method, entry.getValue().getJavaProperty()))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toSet());
                    methodsToRemove.forEach(map::remove);
                },
                method -> method.isAnnotationPresent(DiffedIgnore.class)
        );
    }



    private DifferPropertyParser getParserBean(Class<? extends DifferPropertyParser> parserClass) {
        return applicationContext.getBean(parserClass);
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}