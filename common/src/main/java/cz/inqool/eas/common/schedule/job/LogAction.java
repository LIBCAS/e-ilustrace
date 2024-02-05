package cz.inqool.eas.common.schedule.job;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

public enum LogAction implements Labeled {
    ALWAYS("Vždy"),
    ON_ERROR("Při chybě"),
    NEVER("Nikdy");

    @Getter
    private final String label;

    LogAction(String label ) {
        this.label = label;
    }

    @Override
    public String getId() {
        return name();
    }
}
