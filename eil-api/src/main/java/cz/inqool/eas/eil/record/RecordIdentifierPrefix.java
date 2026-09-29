package cz.inqool.eas.eil.record;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RecordIdentifierPrefix implements Labeled {
    BCBT("BCBT"),
    INC("INC"),
    K("K");

    @Getter
    private final String label;

    @Override
    public String getId() {
        return name();
    }
}