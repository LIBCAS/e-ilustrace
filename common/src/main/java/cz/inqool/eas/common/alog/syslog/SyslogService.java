package cz.inqool.eas.common.alog.syslog;

import com.cloudbees.syslog.*;
import com.cloudbees.syslog.sender.SyslogMessageSender;
import cz.inqool.eas.common.alog.event.Event;
import cz.inqool.eas.common.alog.event.EventSeverity;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.GeneralException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;

import java.io.CharArrayWriter;
import java.util.Date;

@Slf4j
public class SyslogService {
    /**
     * Application's process ID.
     */
    @Setter
    private String pid;

    /**
     * Application's name.
     */
    @Setter
    private String appName;

    /**
     * Application's facility.
     */
    @Setter
    private Facility facility;

    private SyslogMessageSender messageSender;

    /**
     * Sends syslog message.
     *
     * @param event Audit log event that should be send as syslog message.
     */
    @CircuitBreaker(GeneralException.class)
    public void sendEvent(Event event) {
        SyslogMessage message = translateEvent(event);

        try {
            messageSender.sendMessage(message);
        } catch (Exception ex) {
            throw new GeneralException(ex);
        }
    }

    /**
     * Logs failed attempt of sending syslog message.
     *
     * @param event Audit log event for that syslog message sending failed.
     */
    @Recover
    public void recover(Event event) {
        log.error("Failed to send syslog message {}. Logging only internally.", event);
    }

    private SyslogMessage translateEvent(Event event) {
        CharArrayWriter writer = new CharArrayWriter();
        writer.append(event.getMessage());

        SyslogMessage message = new SyslogMessage();
        message.setAppName(appName);
        message.setFacility(facility);
        message.setSeverity(translateSeverity(event.getSeverity()));
        message.setTimestamp(Date.from(event.getCreated()));
        message.setProcId(this.pid);
        message.setMsg(writer);

        SDElement eas = new SDElement("eas@");
        message.withSDElement(eas);

        eas.addSDParam(new SDParam("msg", event.getMessage()));
        eas.addSDParam(new SDParam("source", event.getSource()));

        String ipAddress = event.getIpAddress();
        if (ipAddress != null) {
            eas.addSDParam(new SDParam("userIp", ipAddress));
        }

        UserReference createdBy = event.getUser();
        if (createdBy != null) {
            eas.addSDParam(new SDParam("userId", createdBy.getId()));
            eas.addSDParam(new SDParam("userName", createdBy.getName()));
        }

        return message;
    }

    private Severity translateSeverity(EventSeverity eventSeverity) {
        if (eventSeverity == null) {
            return Severity.NOTICE;
        }

        switch (eventSeverity) {
            case INFO:
                return Severity.INFORMATIONAL;
            case WARN:
                return Severity.WARNING;
            case DEBUG:
                return Severity.DEBUG;
            case ERROR:
                return Severity.ERROR;
            default:
                return Severity.NOTICE;
        }
    }

    @Autowired
    public void setMessageSender(SyslogMessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
