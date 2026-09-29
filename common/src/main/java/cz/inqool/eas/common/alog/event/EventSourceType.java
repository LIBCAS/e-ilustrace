package cz.inqool.eas.common.alog.event;


import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event source type.
 */
@AllArgsConstructor
public enum EventSourceType implements Labeled {
    USER("Uživatel"),
    SYSTEM("Systém"),
    EXTERNAL("Externí systém");

    @Getter
    private final String label;

    @Override
    public String getId() {
        return name();
    }
}
