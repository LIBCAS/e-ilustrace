package cz.inqool.eas.common.security.personal;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

public enum PersonalEventType implements Labeled {
    LOGIN_SUCCESSFUL("Uspěšné přihlášení"),
    LOGIN_FAILED("Neuspěšné přihlášení"),
    LOGOUT("Odhlášení"),
    LOGOUT_AUTOMATIC("Automatické odhlášení");

    @Getter
    private final String label;

    PersonalEventType(String label) {
        this.label = label;
    }


    @Override
    public String getId() {
        return name();
    }
}
