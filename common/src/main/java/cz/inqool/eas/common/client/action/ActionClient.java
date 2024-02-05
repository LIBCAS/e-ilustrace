package cz.inqool.eas.common.client.action;

import cz.inqool.eas.common.client.ClientRequestBuilder;
import cz.inqool.eas.common.client.action.dto.ClientActionDetail;
import cz.inqool.eas.common.security.service.ServiceHub;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.session.Session;

import java.util.function.Supplier;

/**
 * Client for action subsystem using inter-service communication.
 */
@Slf4j
public class ActionClient {
    private Supplier<Session> clientSession;

    private ServiceHub<? extends Session> serviceHub;

    private final String serviceUrl;

    private final String actionsPath;

    @Builder
    public ActionClient(String serviceUrl, String actionsPath) {
        this.serviceUrl = serviceUrl;
        this.actionsPath = actionsPath;
    }

    public ClientActionDetail getActionByCode(String code) {
        Session session = clientSession.get();

        try {
            return serviceHub.doInSession(session, (template) -> {
                return ClientRequestBuilder.<ClientActionDetail>get()
                        .urlPath(actionsPath + "/coded/" + code)
                        .headers()
                        .setContentType(MediaType.APPLICATION_JSON).set()
                        .responseType(ClientActionDetail.class)
                        .execute(template, serviceUrl);
            });
        } catch (Exception exception) {
            log.error("Failed to generate report.", exception);
            throw new ActionClientException("Failed to get action with code '" + code + "'.", exception);
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
