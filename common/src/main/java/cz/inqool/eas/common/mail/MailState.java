package cz.inqool.eas.common.mail;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

public enum MailState implements Labeled {
    QUEUED("Ve frontě"),
    SENT("Odeslaný"),
    CANCELED("Zrušený"),
    ERROR("Chyba");

    @Getter
    private final String label;

    MailState(String label ) {
        this.label = label;
    }

    @Override
    public String getId() {
        return name();
    }
}
