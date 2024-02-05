package cz.inqool.eas.common.domain.index.reindex.reference;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.DomainIndexed;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * Class representing reference to another entity in an indexed object class
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class IndexReferenceField {

    /**
     * Indexed object type owning the reference
     */
    private final Class<? extends DomainIndexed<?, ?>> indexedType;

    /**
     * Type of referenced entity
     */
    private final Class<? extends Domain<?>> referencedClass;

    /**
     * Elasticsearch path to the referenced entity ID mapped in the owning index to effectively search all entity items
     * to reindex
     */
    private final String elasticSearchPath;

    /**
     * Register reference to be updated only when these fields were updated. Null value means to update the reference
     * when any field of referenced entity is changed
     */
    private Set<String> onlyOnChanged;
}
