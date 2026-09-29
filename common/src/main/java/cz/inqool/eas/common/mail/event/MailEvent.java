package cz.inqool.eas.common.mail.event;

import cz.inqool.eas.common.mail.Mail;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event fired when an event occurred during mail sending.
 */
public abstract class MailEvent extends ApplicationEvent {
    @Getter
    private final Mail mail;

    public MailEvent(Object service, Mail mail) {
        super(service);

        this.mail = mail;
    }
}
