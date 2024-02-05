package cz.inqool.eas.common.certificate;

import cz.inqool.eas.common.dictionary.index.DictionaryIndexedObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "eas_certificate")
public class CertificateIndexedObject extends DictionaryIndexedObject<Certificate, Certificate> {
    @Override
    public void toIndexedObject(Certificate obj) {
        super.toIndexedObject(obj);
    }
}
