package cz.inqool.eas.common.domain.index.reindex.reference.entity;

import cz.inqool.eas.common.dated.store.InstantGenerator;
import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.reindex.reference.IndexReferenceField;
import cz.inqool.eas.common.domain.store.DomainObject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Stores information about an update of some entity, which is referenced from indexed object class of different
 * entity/entities and therefore should be reindexed there.
 * <p>
 * Serves as permanent storage accessed asynchronously, so no inconsistency will be present even in case of server
 * failure (crash).
 */
@Getter
@Setter
@Entity
@Table(name = "eas_index_reference_update")
@BatchSize(size = 100)
public class IndexReferenceUpdate extends DomainObject<IndexReferenceUpdate> {

    /**
     * Created timestamp
     */
    @Column(updatable = false, nullable = false)
    @GeneratorType(type = InstantGenerator.class, when = GenerationTime.INSERT)
    private Instant created;

    /**
     * Indexed type of entity that needs to be reindexed
     */
    @NotNull
    private Class<? extends DomainIndexed<?, ?>> indexedType;

    /**
     * Elasticsearch path to the field used to find only the affected entity entries (not every entity needs to have
     * actual reference to the updated entity)
     */
    private String elasticSearchPath;

    /**
     * Id of updated entity, that may be referenced from {@link #indexedType}
     */
    @NotNull
    private String updatedEntityId;


    public static IndexReferenceUpdate of(@NonNull IndexReferenceField indexReferenceField, @NonNull Domain<?> updatedEntity) {
        IndexReferenceUpdate referenceUpdate = new IndexReferenceUpdate();
        referenceUpdate.setIndexedType(indexReferenceField.getIndexedType());
        referenceUpdate.setElasticSearchPath(indexReferenceField.getElasticSearchPath());
        referenceUpdate.setUpdatedEntityId(updatedEntity.getId());

        return referenceUpdate;
    }
}
