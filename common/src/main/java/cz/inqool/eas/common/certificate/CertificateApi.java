package cz.inqool.eas.common.certificate;

import cz.inqool.eas.common.dictionary.DictionaryApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "PFX and JKS certificates", description = "Backend certificates CRUD API")
@ResponseBody
@RequestMapping("${certificate.url}")
public class CertificateApi extends DictionaryApi<
        Certificate,
        CertificateDetail,
        CertificateList,
        CertificateCreate,
        CertificateUpdate,
        CertificateService> {
}
