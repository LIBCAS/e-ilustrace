package cz.inqool.eas.common.client.export;

import cz.inqool.eas.common.client.ClientRequestBuilder;
import cz.inqool.eas.common.export.request.ExportRequest;
import cz.inqool.eas.common.export.request.dto.FinishProcessingDto;
import cz.inqool.eas.common.export.request.dto.SignalErrorDto;
import cz.inqool.eas.common.security.service.ServiceHub;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.session.Session;

import java.util.function.Supplier;

/**
 * Client for export subsystem using inter-service communication.
 */
@Slf4j
public class ExportClient {
    private Supplier<Session> clientSession;

    private ServiceHub<? extends Session> serviceHub;

    private final String serviceUrl;

    private final String requestsPath;

    @Builder
    public ExportClient(String serviceUrl, String requestsPath) {
        this.serviceUrl = serviceUrl;
        this.requestsPath = requestsPath;
    }

    public ExportRequest acquire() {
        Session session = clientSession.get();

        try {
            return serviceHub.doInSession(session, (template) -> {
                return ClientRequestBuilder.<ExportRequest>post()
                        .urlPath(requestsPath + "/acquire")
                        .headers()
                        .setContentType(MediaType.APPLICATION_JSON).set()
                        .responseType(ExportRequest.class)
                        .execute(template, serviceUrl);
            });
        } catch (Exception exception) {
            log.error("Failed to acquire export request.", exception);
            throw new ExportClientException("Failed to acquire export request.", exception);
        } finally {
            serviceHub.destroySession(session);
        }
    }

    public ExportRequest acquireExact(String id) {
        Session session = clientSession.get();

        try {
            return serviceHub.doInSession(session, (template) -> {
                return ClientRequestBuilder.<ExportRequest>post()
                        .urlPath(requestsPath + "/" + id + "/acquire")
                        .headers()
                        .setContentType(MediaType.APPLICATION_JSON).set()
                        .responseType(ExportRequest.class)
                        .execute(template, serviceUrl);
            });
        } catch (Exception exception) {
            log.error("Failed to acquire export request.", exception);
            throw new ExportClientException("Failed to acquire export request.", exception);
        } finally {
            serviceHub.destroySession(session);
        }
    }

    public void finishProcessing(String id, FinishProcessingDto dto) {
        Session session = clientSession.get();

        try {
            serviceHub.doInSession(session, (template) -> {
                ClientRequestBuilder.<Void>post()
                        .urlPath(requestsPath + "/" + id + "/finish-processing")
                        .headers()
                        .setContentType(MediaType.APPLICATION_JSON).set()
                        .payload(dto)
                        .responseType(Void.class)
                        .execute(template, serviceUrl);
            });
        } catch (Exception exception) {
            log.error("Failed to upload generated export.", exception);
            throw new ExportClientException("Failed to upload generated export.", exception);
        } finally {
            serviceHub.destroySession(session);
        }
    }

    public void signalError(String id, SignalErrorDto dto) {
        Session session = clientSession.get();

        try {
            serviceHub.doInSession(session, (template) -> {
                ClientRequestBuilder.<Void>post()
                        .urlPath(requestsPath + "/" + id + "/signal-error")
                        .headers()
                        .setContentType(MediaType.APPLICATION_JSON).set()
                        .payload(dto)
                        .responseType(Void.class)
                        .execute(template, serviceUrl);
            });
        } catch (Exception exception) {
            log.error("Failed to signal error during export processing.", exception);
            throw new ExportClientException("Failed to signal error during export processing.", exception);
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
