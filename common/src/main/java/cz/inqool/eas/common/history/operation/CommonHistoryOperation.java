package cz.inqool.eas.common.history.operation;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.Getter;

public enum CommonHistoryOperation implements Labeled {
    CREATE("Vytvoření"),
    UPDATE("Oprava"),
    DELETE("Smazání"),
    ACTIVATE("Aktivace"),
    DEACTIVATE("Deaktivace"),
    OTHER("Ostatní");

    @Getter
    private final String label;

    CommonHistoryOperation(String label) {
        this.label = label;
    }


    @Override
    public String getId() {
        return name();
    }

    public HistoryOperationReference toReference() {
        return new HistoryOperationReference(this.getId(), this.getLabel());
    }
}
