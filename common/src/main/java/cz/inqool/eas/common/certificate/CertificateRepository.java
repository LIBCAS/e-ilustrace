package cz.inqool.eas.common.certificate;

import cz.inqool.eas.common.dictionary.DictionaryRepository;
import cz.inqool.eas.common.dictionary.index.DictionaryIndex;
import cz.inqool.eas.common.dictionary.store.DictionaryStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import static cz.inqool.eas.common.module.Modules.CERTIFICATES;

public class CertificateRepository extends DictionaryRepository<
        Certificate,
        Certificate,
        CertificateIndexedObject,
        DictionaryStore<Certificate, Certificate, QCertificate>,
        DictionaryIndex<Certificate, Certificate, CertificateIndexedObject>> {

    @Override
    protected ModuleDefinition getModule() {
        return CERTIFICATES;
    }
}
