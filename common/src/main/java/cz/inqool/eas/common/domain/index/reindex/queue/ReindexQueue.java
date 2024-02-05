package cz.inqool.eas.common.domain.index.reindex.queue;

import cz.inqool.eas.common.dated.store.InstantGenerator;
import cz.inqool.eas.common.domain.store.DomainObject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

/**
 * Entity class for storing repositories in reindex queue
 * 
 * Main usage:
 * When making changes to index classes, you use liquibase changelog 
 * to insert modified classes into this queue. 
 * After deploy, they get automatically popped from the queue and reindexed.
 * 
 * Use insert if not exists to prevent double reindex.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class ReindexQueue<RQ_ENTITY> extends DomainObject<RQ_ENTITY> {

    /**
     * Repository class name
     */
    String repositoryClass;

    /**
     * Create timestamp
     */
    @Column(updatable = false, nullable = false)
    @GeneratorType(type = InstantGenerator.class, when = GenerationTime.INSERT)
    Instant created;
}
