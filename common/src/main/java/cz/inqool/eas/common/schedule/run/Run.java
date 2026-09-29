package cz.inqool.eas.common.schedule.run;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.schedule.job.Job;
import cz.inqool.entityviews.Viewable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.Instant;

/**
 * The run of time-based job.
 */
@Viewable
@DomainViews
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "eas_schedule_run")

public class Run extends DatedObject<Run> {
    /**
     * The job of the run.
     */
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected Job job;

    /**
     * Log from console.
     */
    protected String console;

    /**
     * Object result of the job in JSON format.
     */
    protected String result;

    /**
     * Run's state.
     */
    @Enumerated(EnumType.STRING)
    protected RunState state;

    /**
     * Time of the run's start.
     */
    protected Instant startTime;

    /**
     * Time of the run's end.
     */
    protected Instant endTime;
}
