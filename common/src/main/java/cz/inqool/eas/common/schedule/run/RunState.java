package cz.inqool.eas.common.schedule.run;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

public enum RunState implements Labeled {
    STARTED("Běží"),
    ERROR("Chyba"),
    FINISHED("Skončil");


    @Getter
    private final String label;

    RunState(String label ) {
        this.label = label;
    }

    @Override
    public String getId() {
        return name();
    }
}
