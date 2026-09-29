package cz.inqool.eas.common.mail.event;

import cz.inqool.eas.common.mail.Mail;

/**
 * Event fired when an error occurred during mail sending.
 */
public class MailErrorEvent extends MailEvent {
    public MailErrorEvent(Object service, Mail mail) {
        super(service, mail);
    }
}
