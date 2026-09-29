package cz.inqool.eas.common.schedule.job;

import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class JobScheduler implements ApplicationListener<ContextRefreshedEvent> {
    private JobRepository repository;

    private JobExecutor executor;

    private TaskScheduler scheduler;

    private final ConcurrentMap<String, JobDescriptor> runningJobs = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, JobDescriptor> extraJobs = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        Collection<Job> jobs = repository.listAll();

        log.info("Starting jobs...");

        jobs.forEach(this::updateJobSchedule);
    }

    protected void updateJobSchedule(Job job) {
        try {
            JobDescriptor old = runningJobs.remove(job.getId());
            if (old != null) {
                log.info("Disabling {} ({})", job, job.getName());
                old.future.cancel(false);
            }

            if (job.isValidAndActive() == Boolean.TRUE) {
                log.info("Enabling {} ({})", job, job.getName());

                JobDescriptor descriptor = new JobDescriptor();
                descriptor.future = scheduler.schedule(() -> {
                    try {
                        descriptor.running = true;
                        executor.executeJob(job);
                    } finally {
                        descriptor.running = false;
                    }
                }, new CronTrigger(job.getTimer()));

                runningJobs.put(job.getId(), descriptor);
            }
        } catch (Exception ex) {
            log.error("Failed to schedule '{}'", job);
        }
    }

    public void scheduleJobImmediately(Job job) {
        try {

            log.info("Scheduling job immediately {} ({})", job, job.getName());

            if (extraJobs.containsKey(job.getId())) {
                throw new ForbiddenOperation("WRONG_STATE")
                        .details(details -> details.id(job.getId()).property("type", Job.class.getSimpleName()))
                        .debugInfo(info -> info.clazz(Job.class));
            }

            JobDescriptor descriptor = new JobDescriptor();
            descriptor.future = scheduler.schedule(() -> {
                try {
                    descriptor.running = true;
                    executor.executeJob(job);
                } finally {
                    descriptor.running = false;
                    extraJobs.remove(job.getId());
                }
            }, Instant.now());
            extraJobs.put(job.getId(), descriptor);

        } catch (Exception ex) {
            log.error("Failed to schedule '{}'", job);
        }
    }

    public void stopJobImmediately(Job job) {
        try {

            log.info("Stopping job immediately {} ({})", job, job.getName());

            JobDescriptor extraJob = extraJobs.get(job.getId());
            JobDescriptor normalJob = runningJobs.get(job.getId());

            if (extraJob != null) {
                extraJob.future.cancel(true);
                extraJobs.remove(job.getId());
            }

            if (normalJob != null) {
                normalJob.future.cancel(true);
                updateJobSchedule(job);
            }
        } catch (Exception ex) {
            log.error("Failed to schedule '{}'", job);
        }
    }

    public boolean isJobRunning(Job job) {
        JobDescriptor extraJob = extraJobs.get(job.getId());
        JobDescriptor normalJob = runningJobs.get(job.getId());

        if (extraJob != null) {
            return extraJob.running;
        } else if (normalJob != null) {
            return normalJob.running;
        } else {
            return false;
        }
    }

    @Autowired
    public void setRepository(JobRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setExecutor(JobExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setScheduler(@Qualifier("eas-scheduling") TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    static class JobDescriptor {
        ScheduledFuture<?> future;
        boolean running;
    }
}
