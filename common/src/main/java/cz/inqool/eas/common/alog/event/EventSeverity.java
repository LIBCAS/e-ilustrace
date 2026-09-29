package cz.inqool.eas.common.alog.event;


import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Severity of the event.
 */
@AllArgsConstructor
public enum EventSeverity implements Labeled {
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR");

    @Getter
    private final String label;


    @Override
    public String getId() {
        return name();
    }
}
