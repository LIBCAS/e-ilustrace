package cz.inqool.eas.common.mail;

import cz.inqool.eas.common.authored.store.AuthoredStore;


public class MailStore extends AuthoredStore<Mail, Mail, QMail> {
    public MailStore() {
        super(Mail.class);
    }

    public Mail getNextWaiting() {
        QMail model = QMail.mail;

        Mail mail = query().
                select(model).
                from(model).
                where(model.deleted.isNull()).
                where(model.state.eq(MailState.QUEUED)).
                orderBy(model.created.asc()).
                fetchFirst();

        detachAll();

        return mail;
    }
}
