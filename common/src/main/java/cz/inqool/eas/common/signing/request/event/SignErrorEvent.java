package cz.inqool.eas.common.signing.request.event;

import cz.inqool.eas.common.signing.request.SignRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Událost, která se vyvolá při chybě při podepsání dokumentu.
 */
public class SignErrorEvent extends ApplicationEvent {
    @Getter
    private final SignRequest request;

    public SignErrorEvent(Object source, SignRequest request) {
        super(source);
        this.request = request;
    }
}
