package cz.inqool.eas.common.mail;

import cz.inqool.eas.common.mail.event.MailErrorEvent;
import cz.inqool.eas.common.mail.event.MailSentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduler dispatching enqueued mails.
 *
 * TODO: make the timer configurable.
 */
@Slf4j
public class MailScheduler {
    private MailQueue queue;

    private MailSender sender;

    protected ApplicationEventPublisher eventPublisher;

    /**
     * Send all enqueued mails.
     */
    @Scheduled(fixedDelay = 5000)
    public void run() {
        Mail mail;
        while ((mail = queue.getNextWaiting()) != null) {
            try {
                mail.setSent(true);
                mail.setState(MailState.SENT);
                queue.updateMail(mail);

                sender.send(mail);

                eventPublisher.publishEvent(new MailSentEvent(this, mail));
            } catch (Exception e) {
                mail.setError(e.getMessage());
                mail.setState(MailState.ERROR);
                queue.updateMail(mail);

                eventPublisher.publishEvent(new MailErrorEvent(this, mail));
            }
        }
    }

    @Autowired
    public void setQueue(MailQueue queue) {
        this.queue = queue;
    }

    @Autowired
    public void setSender(MailSender sender) {
        this.sender = sender;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
