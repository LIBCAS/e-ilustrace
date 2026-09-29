package cz.inqool.eas.eil.record.illustration;

import cz.inqool.eas.eil.domain.LabeledEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum IconclassThemeState implements LabeledEnum<IconclassThemeState> {
    DONE("Dokončeno"),
    INPROGRESS("Rozpracováno"),
    UNENRICHED("Neobohaceno")
    ;

    @Getter
    private final String label;
}
