package cz.inqool.eas.common.history;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.history.operation.CommonHistoryOperation;
import cz.inqool.eas.common.history.operation.HistoryOperationReference;
import cz.inqool.entityviews.Viewable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

/**
 * Record in simple changelog for objects.
 */
@Viewable
@DomainViews
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "eas_history")
public class History extends AuthoredObject<History> {
    /**
     * Entity's ID for which the history record was created.
     *
     * System relies on the uniqueness of the entity's ID across the system.
     */
    protected String entityId;

    /**
     * Operation reference.
     *
     * Default values are from {@link CommonHistoryOperation}. Application can introduce new codes.
     */
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "operation_id"))
    @AttributeOverride(name = "name", column = @Column(name = "operation_name"))
    protected HistoryOperationReference operation;

    /**
     * Operation's description.
     */
    protected String description;
}
