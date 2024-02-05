package cz.inqool.eas.common.domain.index.mapping;

import cz.inqool.eas.common.domain.DomainRepository;
import cz.inqool.eas.common.domain.index.field.IndexFieldGeoPointLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexFieldNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import cz.inqool.eas.common.utils.AopUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class IndexMappingService {

    private List<DomainRepository<?, ?, ?, ?, ?>> repositories;

    /**
     * Field for storing index mappings of all entities.
     * <p>
     * First call of method {@link #getMappings} computes full map and stores it in this field.
     * Therefore, this field serves as a CACHE for further requests.
     */
    private Map<String, List<String>> cachedIndexMappings;

    /**
     * Obtain index mappings for all provided entities, or all mappings if no entity is specified.
     * <pre>
     * Example:
     * {
     *   "FIRST_ENTITY": [
     *     "firstAttribute",
     *     "secondAttribute.id",
     *     "secondAttribute.name",
     *     "thirdAttribute.inner.id"
     *   ],
     *   "SECOND_ENTITY": [...]
     * }
     * </pre>
     *
     * @param entityNames names of entities for which mappings should be obtained
     * @return index mappings for declared entities, or all mappings if no entity was declared
     */
    public Map<String, List<String>> getMappings(@NonNull List<String> entityNames) {
        if (repositories == null) return Collections.emptyMap();

        if (this.cachedIndexMappings == null) computeIndexMappings();

        // no entity name specified -> return all mappings
        if (entityNames.isEmpty()) return this.cachedIndexMappings;

        // create 'entity:mappingsList' for every entity and merge them into one resulting map
        return entityNames.stream().collect(Collectors.toMap(
                name -> name,
                name -> cachedIndexMappings.getOrDefault(name, List.of()),
                (a, b) -> b)
        );
    }

    /**
     * Compute map of index mappings for every injected repository and store it into field that serves as a cache.
     */
    private void computeIndexMappings() {
        this.cachedIndexMappings = repositories.stream()
                .collect(Collectors.toMap(this::getEntityName, this::parseIndexMappings));
        log.info("Cached computed index mappings.");
    }

    /**
     * Get list of string paths representing index mappings that can be filtered/sorted on.
     *
     * @param repository repository with initialized map of index mappings
     * @return string paths representing index mappings (e.g 'createdBy.id', 'createdBy.name', 'updatedBy.id'...)
     */
    private List<String> parseIndexMappings(DomainRepository<?, ?, ?, ?, ?> repository) {
        IndexObjectFields fieldsMap = repository.getIndex().getIndexObjectFields();

        return fieldsMap.entrySet().stream()
                .filter(this::isLeafNode) // use only LEAF paths, e.g. 'createdBy.id', not 'createdBy'
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Checks if provided entry contains a value that is a LEAF in index fields tree map.
     * <pre>
     * Let's have an example where IndexedObject contains one field
     * with type {@link LabeledReference}, indexed as {@link FieldType#Object}.
     *
     * &#64;Document(indexName = "my_entity")
     * public class MyEntityIndexedObject extends DomainIndexedObject<MyEntity, MyEntity> {
     *     &#64;Field(type = FieldType.Object, fielddata = true)
     *     protected LabeledReference myObject;
     * }
     *
     * Such indexing would create these paths:
     *     1. 'myObject'
     *     2. 'myObject.id'
     *     3. 'myObject.name'
     *
     * Mappings 2. and 3. are LEAF nodes and can be filtered/sorted on (and such mappings will be returned).
     * </pre>
     *
     * @return true if provided map entry contains value that is of LEAF type
     */
    private boolean isLeafNode(Map.Entry<String, IndexFieldNode> entry) {
        IndexFieldNode node = entry.getValue();
        return node instanceof IndexFieldLeafNode || node instanceof IndexFieldGeoPointLeafNode;
    }

    /**
     * Get class name of entity, that is a ROOT type in provided repository.
     *
     * @param repository repository with declared ROOT type for which to get a class name.
     * @return simple class name (without package) of ROOT type from provided repository.
     */
    private String getEntityName(DomainRepository<?, ?, ?, ?, ?> repository) {
        return repository.getRootType().getSimpleName();
    }

    @Autowired(required = false)
    public void setRepositories(List<DomainRepository<?, ?, ?, ?, ?>> repositories) {
        this.repositories = repositories.stream()
                .map(AopUtils::unwrap)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

