package cz.inqool.eas.eil.exhibition;

import cz.inqool.eas.eil.domain.LabeledEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Radio implements LabeledEnum<Radio> {
    ALBUM("Album"),
    STORYLINE("Storyline"),
    SLIDER("Slider"),
    ;

    @Getter
    private final String label;
}