package cz.inqool.eas.eil.record.link;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LinkEnum implements Labeled {
    ILLUSTRATION("ilustrace"),
    ILLUSTRATION_PAGE("strana s ilustrací"),
    FRONT_PAGE("titulní list"),
    DIGITAL_COPY("digitální kopie");

    @Getter
    private final String label;

    @Override
    public String getId() {
        return name();
    }
}
