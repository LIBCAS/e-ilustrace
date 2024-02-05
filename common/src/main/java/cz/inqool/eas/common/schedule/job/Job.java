package cz.inqool.eas.common.schedule.job;

import cz.inqool.eas.common.dictionary.DictionaryViews;
import cz.inqool.eas.common.dictionary.store.DictionaryObject;
import cz.inqool.eas.common.script.ScriptType;
import cz.inqool.entityviews.Viewable;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

import java.time.LocalDateTime;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Time-based job.
 */
@Viewable
@DictionaryViews
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "eas_schedule_job")
public class Job extends DictionaryObject<Job> {
    /**
     * Cron timer.
     */
    protected String timer;

    /**
     * Logging.
     */
    @Enumerated(EnumType.STRING)
    protected LogAction logAction;

    /**
     * Language of the script used
     */
    @Enumerated(EnumType.STRING)
    protected ScriptType scriptType;

    /**
     * Script's source code.
     */
    protected String script;

    /**
     * Execute script in transaction.
     */
    protected boolean useTransaction;

    /**
     * Flag describing whether job is being executed.
     */
    @Transient
    protected boolean running;

    @Transient
    protected LocalDateTime last;

    @Transient
    protected LocalDateTime next;

    /**
     * Description
     */
    @ViewableProperty(views = {DEFAULT, CREATE, UPDATE, DETAIL, LIST})
    protected String description;
}
