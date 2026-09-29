package cz.inqool.eas.common.sequence;

import cz.inqool.eas.common.dictionary.store.DictionaryObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.entityviews.Viewable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Nationalized;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.DecimalFormat;


/**
 * Number sequence.
 */
@Viewable
@DomainViews
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "eas_sequence")
public class Sequence extends DictionaryObject<Sequence> {

    @Nationalized
    protected String description;

    /**
     * Format of the sequence suitable for {@link DecimalFormat} usage.
     * <p>
     * E.g. ISPV-2016/00 is escaped as ISPV'-2'016/00. For further details about the format see <a
     * href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/DecimalFormat.html">DecimalFormat</a>.
     * </p>
     */
    protected String format;

    /**
     * Current value of the counter
     */
    protected Long counter;

    /**
     * If the sequence is only for tenant.
     */
    protected boolean local;
}
