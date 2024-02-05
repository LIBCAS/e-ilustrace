package cz.inqool.eas.common.ws.soap.logger.interceptor;

import cz.inqool.eas.common.ws.soap.logger.interceptor.extract.MessageBodyExtractor;
import cz.inqool.eas.common.ws.soap.logger.message.SoapMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;

import java.util.List;
import java.util.function.BiFunction;

public class ServerMessageInterceptorFactory {

    private SoapMessageRepository soapMessageRepository;

    private TransactionTemplate transactionTemplate;
    private List<MessageBodyExtractor> messageBodyExtractors;


    public ServerMessageInterceptor createInstance(String serviceName, BiFunction<MessageContext, MethodEndpoint, Boolean> shouldIntercept) {
        return new ServerMessageInterceptor(serviceName, soapMessageRepository, transactionTemplate, shouldIntercept, messageBodyExtractors);
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
