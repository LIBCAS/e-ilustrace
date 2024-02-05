package cz.inqool.eas.common.mail.event;

import cz.inqool.eas.common.mail.Mail;

/**
 * Event fired when a mail is successfully sent.
 */
public class MailSentEvent extends MailEvent {
    public MailSentEvent(Object service, Mail mail) {
        super(service, mail);
    }
}
