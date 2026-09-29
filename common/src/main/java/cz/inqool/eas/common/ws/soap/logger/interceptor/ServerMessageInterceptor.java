package cz.inqool.eas.common.ws.soap.logger.interceptor;

import cz.inqool.eas.common.ws.soap.logger.SoapLoggerUtils;
import cz.inqool.eas.common.ws.soap.logger.interceptor.extract.MessageBodyExtractor;
import cz.inqool.eas.common.ws.soap.logger.message.SoapMessage;
import cz.inqool.eas.common.ws.soap.logger.message.SoapMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.SmartEndpointInterceptor;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.server.endpoint.interceptor.EndpointInterceptorAdapter;
import org.springframework.ws.transport.context.TransportContextHolder;

import java.util.List;
import java.util.function.BiFunction;

import static cz.inqool.eas.common.ws.soap.logger.SoapLoggerUtils.getContextProperty;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

@Slf4j
public class ServerMessageInterceptor extends EndpointInterceptorAdapter implements SmartEndpointInterceptor {

    private static final String SOAP_EXECUTION_START         = "SOAP_EXECUTION_START";
    private static final String SOAP_ACTION                  = "SOAP_ACTION";
    private static final String SOAP_MESSAGE_BODY_EXTRACTOR  = "SOAP_MESSAGE_BODY_EXTRACTOR";
    private static final String SOAP_MESSAGE_OBJECT_PROPERTY = "SOAP_MESSAGE_OBJECT_PROPERTY";

    private final SoapMessageRepository messageRepository;

    private final TransactionTemplate transactionTemplate;

    private final String serviceName;

    private final BiFunction<MessageContext, MethodEndpoint, Boolean> shouldIntercept;

    private final List<MessageBodyExtractor> messageBodyExtractors;


    public ServerMessageInterceptor(String serviceName, SoapMessageRepository messageRepository, TransactionTemplate transactionTemplate, BiFunction<MessageContext, MethodEndpoint, Boolean> shouldIntercept, List<MessageBodyExtractor> messageBodyExtractors) {
        this.serviceName = serviceName;
        this.messageRepository = messageRepository;
        this.transactionTemplate = transactionTemplate;
        this.shouldIntercept = shouldIntercept;
        this.messageBodyExtractors = messageBodyExtractors;
    }


    @Override
    public boolean shouldIntercept(MessageContext messageContext, Object endpoint) {
        return shouldIntercept.apply(messageContext, (MethodEndpoint) endpoint);
    }

    @Override
    public boolean handleRequest(MessageContext context, Object endpoint) {
        MethodEndpoint methodEndpoint = (MethodEndpoint) endpoint;
        if (!shouldIntercept.apply(context, methodEndpoint)) {
            // skip
            return true;
        }

        context.setProperty(SOAP_EXECUTION_START, System.currentTimeMillis());

        String soapAction = SoapLoggerUtils.getSoapAction(methodEndpoint);
        String localPart = SoapLoggerUtils.getLocalPart(methodEndpoint);
        MessageBodyExtractor messageBodyExtractor = getMessageBodyExtractor(context, soapAction);
        context.setProperty(SOAP_ACTION, soapAction);
        context.setProperty(SOAP_MESSAGE_BODY_EXTRACTOR, messageBodyExtractor);

        SoapMessage message = new SoapMessage();
        message.setService(serviceName);
        message.setLocalPart(localPart);
        message.setRequest(messageBodyExtractor.extractRequestBody(context, soapAction));

        transactionTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        transactionTemplate.execute(status -> messageRepository.create(message));
        context.setProperty(SOAP_MESSAGE_OBJECT_PROPERTY, message);

        return true;
    }

    @Override
    public void afterCompletion(MessageContext context, Object endpoint, Exception ex) {
        if (!shouldIntercept.apply(context, (MethodEndpoint) endpoint)) {
            // skip
            return;
        }

        String soapAction = getContextProperty(context, SOAP_ACTION);
        MessageBodyExtractor messageBodyExtractor = getContextProperty(context, SOAP_MESSAGE_BODY_EXTRACTOR);

        SoapMessage message = getContextProperty(context, SOAP_MESSAGE_OBJECT_PROPERTY);
        message.setDuration(computeDuration(context));
        message.setResponse(messageBodyExtractor.extractResponseBody(context, soapAction, ex));
        message.setHttpStatus(SoapLoggerUtils.getResponseCode(TransportContextHolder.getTransportContext()));
        if (ex != null) {
            message.setError(ex.toString());
        }

        transactionTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        transactionTemplate.execute(status -> messageRepository.update(message));
    }

    private long computeDuration(MessageContext context) {
        long start = (Long) context.getProperty(SOAP_EXECUTION_START);
        long end = System.currentTimeMillis();
        return end - start;
    }

    private MessageBodyExtractor getMessageBodyExtractor(@NonNull MessageContext context, @NonNull String soapAction) {
        return messageBodyExtractors.stream()
                .filter(extractor -> extractor.isFor(context, soapAction))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No message body extractor configured"));
    }
}
