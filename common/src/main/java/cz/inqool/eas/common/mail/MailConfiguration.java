package cz.inqool.eas.common.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for mail subsystem.
 *
 * If application wants to use mail subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
@EnableScheduling
public abstract class MailConfiguration {

    /**
     * Constructs {@link MailStore} bean.
     */
    @Bean
    public MailStore mailStore() {
        return new MailStore();
    }

    /**
     * Constructs {@link MailQueue} bean.
     */
    @Bean
    public MailQueue mailService() {
        return new MailQueue();
    }

    /**
     * Constructs {@link MailSender} bean.
     */
    @Bean
    public MailSender mailSender() {
        return new MailSender();
    }

    /**
     * Constructs {@link MailScheduler} bean.
     */
    @Bean
    public MailScheduler mailScheduler() {
        return new MailScheduler();
    }
}
