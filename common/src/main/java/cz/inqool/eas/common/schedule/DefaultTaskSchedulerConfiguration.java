package cz.inqool.eas.common.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class DefaultTaskSchedulerConfiguration {

    /**
     * Default task scheduler used when no other task scheduler is specified/preferred (i.e. when an {@link Scheduled}
     * annotation is used.)
     */
    @Bean("taskScheduler")
    public ThreadPoolTaskScheduler defaultTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("DefaultScheduler");
        return taskScheduler;
    }
}
