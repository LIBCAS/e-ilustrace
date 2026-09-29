package cz.inqool.eas.common.signing.request;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum SignRequestState implements Labeled {
    NEW("Nová"),
    SIGNED("Podepsaná"),
    CANCELED("Zrušená"),
    ERROR("Chyba");

    @Getter
    protected final String label;

    SignRequestState(String label) {
        this.label = label;
    }

    @Override
    public String getId() {
        return name();
    }

    public static SignRequestState valueOfChecked(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        try {
            return valueOf(code);
        } catch (IllegalArgumentException ex) {
            log.warn("Unsupported code found: {}.", code);
            return null;
        }
    }
}
