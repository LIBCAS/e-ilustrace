package cz.inqool.eas.common.schedule;

import cz.inqool.eas.common.schedule.job.*;
import cz.inqool.eas.common.schedule.run.RunApi;
import cz.inqool.eas.common.schedule.run.RunRepository;
import cz.inqool.eas.common.schedule.run.RunService;
import cz.inqool.eas.common.sequence.SequenceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for schedule subsystem.
 * <p>
 * If application wants to use schedule subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class ScheduleConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link SequenceApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for schedule_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("schedule.job.url", scheduleJobUrl());
        properties.put("schedule.run.url", scheduleRunUrl());

        MapPropertySource propertySource = new MapPropertySource("schedule_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link JobRepository} bean.
     */
    @Bean
    public JobRepository jobRepository() {
        return new JobRepository();
    }

    /**
     * Constructs {@link JobService} bean.
     */
    @Bean
    public JobService jobService() {
        return new JobService();
    }

    /**
     * Constructs {@link JobApi} bean.
     */
    @Bean
    public JobApi jobApi() {
        return new JobApi();
    }

    /**
     * Constructs {@link RunRepository} bean.
     */
    @Bean
    public RunRepository runRepository() {
        return new RunRepository();
    }

    /**
     * Constructs {@link RunService} bean.
     */
    @Bean
    public RunService runService() {
        return new RunService();
    }

    /**
     * Constructs {@link RunApi} bean.
     */
    @Bean
    public RunApi runApi() {
        return new RunApi();
    }

    /**
     * Constructs {@link JobExecutor} bean.
     */
    @Bean
    public JobExecutor jobExecutor() {
        return new JobExecutor();
    }

    /**
     * Constructs {@link JobScheduler} bean.
     */
    @Bean
    public JobScheduler jobScheduler() {
        return new JobScheduler();
    }

    @Bean
    @Qualifier("eas-scheduling")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(poolSize());
        threadPoolTaskScheduler.setThreadNamePrefix("EASSchedule");
        return threadPoolTaskScheduler;
    }

    /**
     * Returns url path of {@link JobApi}.
     */
    protected abstract String scheduleJobUrl();

    /**
     * Returns url path of {@link RunApi}.
     */
    protected abstract String scheduleRunUrl();

    /**
     * Maximum number of concurrent task executors.
     */
    protected abstract int poolSize();
}
