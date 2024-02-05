package cz.inqool.eas.eil.config.reindex.queue;

import cz.inqool.eas.common.domain.index.reindex.queue.ReindexQueue;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "eil_reindex_queue")
@BatchSize(size = 100)
public class EilReindexQueue extends ReindexQueue<EilReindexQueue> {
}
