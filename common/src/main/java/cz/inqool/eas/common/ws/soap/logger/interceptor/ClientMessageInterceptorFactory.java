package cz.inqool.eas.common.ws.soap.logger.interceptor;

import cz.inqool.eas.common.ws.soap.logger.interceptor.extract.MessageBodyExtractor;
import cz.inqool.eas.common.ws.soap.logger.message.SoapMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import java.util.ArrayList;
import java.util.List;

public class ClientMessageInterceptorFactory {

    private SoapMessageRepository soapMessageRepository;

    private TransactionTemplate transactionTemplate;
    private List<MessageBodyExtractor> messageBodyExtractors;

    
    public ClientMessageInterceptor createInstance(String serviceName) {
        return new ClientMessageInterceptor(serviceName, soapMessageRepository, transactionTemplate, messageBodyExtractors);
    }

    public void initWebService(WebServiceGatewaySupport service, String serviceName) {
        List<ClientInterceptor> interceptors = new ArrayList<>();

        ClientInterceptor[] existingInterceptors = service.getWebServiceTemplate().getInterceptors();
        if (existingInterceptors != null) {
            interceptors.addAll(List.of(existingInterceptors));
        }

        interceptors.add(this.createInstance(serviceName));

        service.getWebServiceTemplate().setInterceptors(interceptors.toArray(ClientInterceptor[]::new));
    }


    @Autowired
    public void setSoapMessageRepository(SoapMessageRepository soapMessageRepository) {
        this.soapMessageRepository = soapMessageRepository;
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Autowired
    public void setMessageBodyExtractors(List<MessageBodyExtractor> extractors) {
        this.messageBodyExtractors = extractors;
    }
}
