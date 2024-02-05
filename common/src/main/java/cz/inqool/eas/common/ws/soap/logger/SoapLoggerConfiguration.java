package cz.inqool.eas.common.ws.soap.logger;

import cz.inqool.eas.common.ws.soap.logger.interceptor.ClientMessageInterceptorFactory;
import cz.inqool.eas.common.ws.soap.logger.interceptor.ServerMessageInterceptorFactory;
import cz.inqool.eas.common.ws.soap.logger.message.SoapMessageApi;
import cz.inqool.eas.common.ws.soap.logger.message.SoapMessageRepository;
import cz.inqool.eas.common.ws.soap.logger.message.SoapMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.annotation.PostConstruct;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for SOAP logger subsystem.
 * <p>
 * If application wants to use SOAP logger subsystem, it needs to extend this class and add {@link Configuration}
 * annotation.
 */
@Slf4j
public abstract class SoapLoggerConfiguration {

    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link SoapMessageApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for soap_logger_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("soap-logger.message.url", soapLoggerMessageUrl());
        properties.put("soap-logger.message.delete.cron", soapMessageDeleteCron());
        properties.put("soap-logger.message.repository.reindex.batch-size", soapMessageRepositoryReindexBatchSize());

        MapPropertySource propertySource = new MapPropertySource("soap_logger_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link SoapMessageRepository} bean.
     */
    @Bean
    public SoapMessageRepository soapMessageRepository() {
        return new SoapMessageRepository();
    }

    /**
     * Constructs {@link SoapMessageService} bean.
     */
    @Bean
    public SoapMessageService soapMessageService() {
        SoapMessageService service = new SoapMessageService();
        service.setSoapMessageMaxLimit(soapMessageMaxLimit());
        service.setDeleteAfterValue(soapMessageDeleteAfterValue());
        service.setDeleteAfterUnit(soapMessageDeleteAfterUnit());
        return service;
    }

    /**
     * Constructs {@link SoapMessageApi} bean.
     */
    @Bean
    public SoapMessageApi soapMessageApi() {
        return new SoapMessageApi();
    }

    @Bean
    public ClientMessageInterceptorFactory clientMessageInterceptorFactory() {
        return new ClientMessageInterceptorFactory();
    }

    @Bean
    public ServerMessageInterceptorFactory serverMessageInterceptorFactory() {
        return new ServerMessageInterceptorFactory();
    }

    /**
     * Returns url path of {@link SoapMessageApi}.
     */
    protected abstract String soapLoggerMessageUrl();

    /**
     * CRON expression for delete job scheduling.
     * <p>
     * Override to enable automatic SOAP message deleting. Disabled by default.
     */
    protected String soapMessageDeleteCron() {
        return "-";
    }

    /**
     * Max limit of log records stored.
     * <p>
     * If {@code null}, no limit will be set and records won't be deleted. Override to provide custom limit for SOAP
     * message records.
     */
    protected Long soapMessageMaxLimit() {
        return null;
    }

    /**
     * Amount of {@link #soapMessageDeleteAfterUnit}s after which the log records will be deleted.
     * <p>
     * If {@code null}, no value is set and records won't be deleted. Override to provide custom value for deleting old
     * SOAP message records.
     */
    protected Long soapMessageDeleteAfterValue() {
        return null;
    }

    /**
     * Date unit for {@link #soapMessageDeleteAfterValue}
     *
     * Supported values are:
     * <ul>
     *     <li>{@link ChronoUnit#NANOS}</li>
     *     <li>{@link ChronoUnit#MICROS}</li>
     *     <li>{@link ChronoUnit#MILLIS}</li>
     *     <li>{@link ChronoUnit#SECONDS}</li>
     *     <li>{@link ChronoUnit#MINUTES}</li>
     *     <li>{@link ChronoUnit#HOURS}</li>
     *     <li>{@link ChronoUnit#HALF_DAYS}</li>
     *     <li>{@link ChronoUnit#DAYS}</li>
     *     <li>{@link ChronoUnit#WEEKS}</li>
     *     <li>{@link ChronoUnit#MONTHS}</li>
     *     <li>{@link ChronoUnit#YEARS}</li>
     *     <li>{@link ChronoUnit#DECADES}</li>
     *     <li>{@link ChronoUnit#CENTURIES}</li>
     *     <li>{@link ChronoUnit#MILLENNIA}</li>
     *     <li>{@link ChronoUnit#ERAS}</li>
     * </ul>
     */
    protected ChronoUnit soapMessageDeleteAfterUnit() {
        return ChronoUnit.DAYS;
    }

    /**
     * Override to set reindex batch size on SoapMessage repository
     *
     * @see SoapMessageRepository#getReindexBatchSize()
     */
    protected int soapMessageRepositoryReindexBatchSize() {
        return 10000;
    }
}
