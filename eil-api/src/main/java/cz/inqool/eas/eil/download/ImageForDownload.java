package cz.inqool.eas.eil.download;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ImageForDownload implements Labeled {
    ILLUSTRATION("ilustrace"),
    ILLUSTRATION_PAGE("strana s ilustrací"),
    FRONT_PAGE("titulní list");

    @Getter
    private final String label;

    @Override
    public String getId() {
        return name();
    }
}
