package cz.inqool.eas.common.client.file;

import cz.inqool.eas.common.client.ClientRequestBuilder;
import cz.inqool.eas.common.security.service.ServiceHub;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.common.storage.file.OpenedFile;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.util.function.Supplier;

import static java.util.List.of;

/**
 * Client for file subsystem using inter-service communication.
 */
@Slf4j
public class FileClient {
    private Supplier<Session> clientSession;

    private ServiceHub<? extends Session> serviceHub;

    private final String serviceUrl;

    private final String filesPath;

    @Builder
    public FileClient(String serviceUrl, String filesPath) {
        this.serviceUrl = serviceUrl;
        this.filesPath = filesPath;
    }

    public OpenedFile download(String id) {
        Session session = clientSession.get();

        try {
            return serviceHub.doInSession(session, (template) -> {
                ResponseEntity<byte[]> response = ClientRequestBuilder.<byte[]>get()
                        .urlPath(filesPath + "/" + id)
                        .headers()
                        .setAccept(of(MediaType.APPLICATION_OCTET_STREAM)).set()
                        .responseType(byte[].class)
                        .executeEntity(template, serviceUrl);

                String fileName = response.getHeaders().getContentDisposition().getFilename();
                MediaType contentType = response.getHeaders().getContentType();
                long length = response.getHeaders().getContentLength();
                byte[] body = response.getBody();

                if (body == null) {
                    return null;
                }

                File file = new File();
                file.setId(id);
                file.setContentType(contentType != null ? contentType.getType() : null);
                file.setSize(length);
                file.setName(fileName);
                return new OpenedFile(file, new ByteArrayInputStream(body));
            });
        } catch (Exception exception) {
            log.error("Failed to download file {}.", id, exception);
            throw new FileClientException("Failed to download file '" + id + "'.", exception);
        } finally {
            serviceHub.destroySession(session);
        }
    }

    public File upload(String fileName, String contentType, byte[] data) {
        Session session = clientSession.get();

        try {
            return serviceHub.doInSession(session, (template) -> {
                MultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
                pdfHeaderMap.add("Content-disposition", "form-data; name=file; filename=" + fileName);
                pdfHeaderMap.add("Content-type", contentType);
                HttpEntity<byte[]> file = new HttpEntity<>(data, pdfHeaderMap);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", file);

                return ClientRequestBuilder.<File>post()
                        .urlPath(filesPath)
                        .headers()
                        .setContentType(MediaType.MULTIPART_FORM_DATA).set()
                        .payload(body)
                        .responseType(File.class)
                        .execute(template, serviceUrl);
            });
        } catch (Exception exception) {
            log.error("Failed to upload data.", exception);
            throw new FileClientException("Failed to upload data.", exception);
        } finally {
            serviceHub.destroySession(session);
        }
    }

    @Autowired
    public void setClientSession(Supplier<Session> clientSession) {
        this.clientSession = clientSession;
    }

    @Autowired
    public void setServiceHub(ServiceHub<? extends Session> serviceHub) {
        this.serviceHub = serviceHub;
    }
}
