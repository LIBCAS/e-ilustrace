package cz.inqool.eas.common.alog.syslog;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.sender.AbstractSyslogMessageSender;
import com.cloudbees.syslog.sender.SyslogMessageSender;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration for syslog subsystem.
 *
 * If application wants to use syslog subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@EnableAsync
@EnableRetry
public abstract class SyslogConfiguration {
    /**
     * Constructs {@link SyslogService} bean.
     */
    @Bean
    public SyslogService syslogService() {
        ApplicationPid applicationPid = new ApplicationPid();

        SyslogService service = new SyslogService();
        service.setAppName(getAppName());
        service.setFacility(getFacility());
        service.setPid(applicationPid.toString());

        return service;
    }

    /**
     * Constructs {@link SyslogObserver} bean.
     */
    @Bean
    public SyslogObserver syslogObserver() {
        return new SyslogObserver();
    }

    /**
     * Constructs {@link SyslogMessageSender} bean for either UDP or TCP communication.
     */
    @Bean
    public SyslogMessageSender messageSender() {
        AbstractSyslogMessageSender messageSender;

        if (isUdp()) {
            messageSender = new UdpSyslogMessageSender();
        } else {
            messageSender = new TcpSyslogMessageSender();
            ((TcpSyslogMessageSender) messageSender).setSsl(isSsl());
            ((TcpSyslogMessageSender) messageSender).setMaxRetryCount(0);    // retrying is done using spring-retry
        }

        messageSender.setSyslogServerHostname(getHostName());
        messageSender.setSyslogServerPort(getPort());
        messageSender.setMessageFormat(getMessageFormat()); // optional, default is RFC 3164
        return messageSender;
    }

    /**
     * Syslog messaging type, either UDP (true) or TCP (false).
     */
    protected abstract boolean isUdp();

    /**
     * Should connection use SSL.
     */
    protected abstract boolean isSsl();

    /**
     * Application's name.
     */
    protected abstract String getAppName();

    /**
     * Syslog facility - type of program that is logging the message.
     */
    protected abstract Facility getFacility();

    /**
     * Hostname or IP of the syslog server to which messages will be sent.
     */
    protected abstract String getHostName();

    /**
     * Port number of the syslog server to which messages will be sent.
     */
    protected abstract int getPort();

    /**
     * Format of Syslog messages.
     */
    protected abstract MessageFormat getMessageFormat();
}
