package cz.inqool.eas.common.schedule.job;

import cz.inqool.eas.common.dictionary.DictionaryService;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.schedule.run.Run;
import cz.inqool.eas.common.schedule.run.RunRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

@Slf4j
public class JobService extends DictionaryService<
        Job,
        JobDetail,
        JobList,
        JobCreate,
        JobUpdate,
        JobRepository
        > {
    private JobScheduler jobScheduler;

    private RunRepository runRepository;

    @Override
    protected void postCreateHook(@NotNull Job object) {
        super.postCreateHook(object);

        jobScheduler.updateJobSchedule(object);
    }

    @Override
    protected void postUpdateHook(@NotNull Job object) {
        super.postUpdateHook(object);

        jobScheduler.updateJobSchedule(object);
    }

    @Override
    protected void postDeleteHook(@NotNull Job object) {
        super.postDeleteHook(object);

        jobScheduler.updateJobSchedule(object);
    }

    @Override
    protected void postGetHook(@NotNull Job object) {
        super.postGetHook(object);

        boolean running = jobScheduler.isJobRunning(object);
        object.setRunning(running);
        object.setNext(calculateNext(object));
        object.setLast(findLast(object));
    }

    @Override
    protected void postListHook(Result<Job> result) {
        super.postListHook(result);

        result.getItems().forEach(object -> {
            boolean running = jobScheduler.isJobRunning(object);
            object.setRunning(running);
            object.setNext(calculateNext(object));
            object.setLast(findLast(object));
        });
    }

    protected LocalDateTime findLast(Job obj) {
        Run lastRun = runRepository.getLast(obj.getId());

        if (lastRun != null) {
            Instant startTime = lastRun.getStartTime();
            return ofInstant(startTime, systemDefault());
        }

        return null;
    }

    protected LocalDateTime calculateNext(Job obj) {
        if (obj.timer != null && obj.isValidAndActive()) {
            try {
                CronExpression expression = CronExpression.parse(obj.timer);
                return expression.next(LocalDateTime.now());
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Starts non running job.
     *
     * @param id Id of job to start
     */
    @Transactional
    public void start(String id) {
        Job job = repository.find(id);
        notNull(job, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", Job.class.getSimpleName()))
                .debugInfo(info -> info.clazz(Job.class))
                .logAll());

        jobScheduler.scheduleJobImmediately(job);
    }

    /**
     * Stops running job.
     *
     * @param id Id of job to cancel
     */
    @Transactional
    public void stop(String id) {
        Job job = repository.find(id);
        notNull(job, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", Job.class.getSimpleName()))
                .debugInfo(info -> info.clazz(Job.class))
                .logAll());

        jobScheduler.stopJobImmediately(job);
    }

    @Autowired
    public void setJobScheduler(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    @Autowired
    public void setRunRepository(RunRepository runRepository) {
        this.runRepository = runRepository;
    }
}
