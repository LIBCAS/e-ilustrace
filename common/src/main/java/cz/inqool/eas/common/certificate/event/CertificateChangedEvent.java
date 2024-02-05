package cz.inqool.eas.common.certificate.event;

import cz.inqool.eas.common.certificate.Certificate;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event, which will be fired upon certificate change, so Webservices can act accordingly.
 */
public class CertificateChangedEvent extends ApplicationEvent {
    @Getter
    private final Certificate certificate;

    public CertificateChangedEvent(Object source, Certificate certificate) {
        super(source);
        this.certificate = certificate;
    }
}
