package cz.inqool.eas.common.intl;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Language implements Labeled {
    CZECH("Česky"),
    ENGLISH("English"),
    GERMAN("Deutsch"),
    SLOVAK("Slovensky");

    @Getter
    private final String label;

    @Override
    public String getId() {
        return name();
    }
}
