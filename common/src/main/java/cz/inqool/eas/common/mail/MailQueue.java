package cz.inqool.eas.common.mail;

import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

/**
 * Service enabling to enqueue mails.
 */
public class MailQueue {
    private MailStore store;

    @Transactional
    public Mail queue(String to, String subject, String content, boolean isHtml, String identifier) {
        Mail mail = new Mail();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setContentType(isHtml ? "text/html" : "text/plain");
        mail.setContent(content);
        mail.setState(MailState.QUEUED);
        mail.setIdentifier(identifier);

        return store.create(mail);
    }

    @Transactional
    public Mail queue(String to, String subject, String content, boolean isHtml) {
        return queue(to, subject, content, isHtml, null);
    }

    @Transactional
    public Mail getNextWaiting() {
        return store.getNextWaiting();
    }

    @Transactional
    public void updateMail(Mail mail) {
        store.update(mail);
    }

    @Autowired
    public void setStore(MailStore store) {
        this.store = store;
    }
}
