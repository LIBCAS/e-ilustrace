package cz.inqool.eas.common.certificate;

import cz.inqool.eas.common.certificate.event.CertificateChangedEvent;
import cz.inqool.eas.common.dictionary.DictionaryService;
import cz.inqool.eas.common.exception.v2.MissingAttribute;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.storage.file.FileResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

@Slf4j
public class CertificateService extends DictionaryService<
        Certificate,
        CertificateDetail,
        CertificateList,
        CertificateCreate,
        CertificateUpdate,
        CertificateRepository
        > {
    private FileManager fileManager;

    @Override
    protected void postCreateHook(Certificate object) {
        super.postCreateHook(object);

        eventPublisher.publishAfterTransactionCommitEvent(new CertificateChangedEvent(this, object));
    }

    @Override
    protected void postUpdateHook(Certificate object) {
        super.postUpdateHook(object);

        eventPublisher.publishAfterTransactionCommitEvent(new CertificateChangedEvent(this, object));
    }

    @Override
    protected void postDeleteHook(Certificate object) {
        super.postDeleteHook(object);

        eventPublisher.publishAfterTransactionCommitEvent(new CertificateChangedEvent(this, object));
    }

    public FileResource getByCodeAsResource(String code) {
        Certificate certificate = this.getInternalByCode(Certificate.class, code);
        notNull(certificate, () -> new MissingObject("NOT_FOUND"));

        File content = certificate.getContent();
        notNull(content, () -> new MissingAttribute("NOT_FOUND").debugInfo("content"));

        return fileManager.openAsResource(content.getId());
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }
}
