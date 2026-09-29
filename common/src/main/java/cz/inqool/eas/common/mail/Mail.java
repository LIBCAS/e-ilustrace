package cz.inqool.eas.common.mail;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.domain.DomainViews;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

/**
 * Enqueued or sent mail.
 *
 * TODO: add support for attachments.
 */
@DomainViews
@Setter
@Getter
@Entity
@Table(name = "eas_mail")
public class Mail extends AuthoredObject<Mail> {
    /**
     * Mail subject.
     */
    @Nationalized
    protected String subject;

    /**
     * Mail content.
     */
    @Nationalized
    protected String content;

    /**
     * Stav odeslání.
     */
    @Enumerated(EnumType.STRING)
    protected MailState state;

    /**
     * Identifikátor.
     */
    protected String identifier;

    /**
     * MIME type.
     */
    protected String contentType;

    /**
     * Mail receiver.
     */
    @Column(name="\"to\"")
    protected String to;

    /**
     * Flag describing whether mail was sent.
     */
    protected boolean sent;

    /**
     * Message of exception thrown during mail sending.
     */
    protected String error;
}
