package cz.inqool.eas.common.certificate.event;

/**
 * Service which will be informed when a certificate changed.
 */
public interface CertificateAwareService {
    void initCertificates();
}
