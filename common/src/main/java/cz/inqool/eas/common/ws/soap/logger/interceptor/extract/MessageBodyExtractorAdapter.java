package cz.inqool.eas.common.ws.soap.logger.interceptor.extract;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;

/**
 * Adapter for message body extractors, providing more convenient extraction methods.
 */
public abstract class MessageBodyExtractorAdapter implements MessageBodyExtractor {

    @Override
    public String extractRequestBody(@NonNull MessageContext context, @NonNull String soapAction) {
        return extractRequestBody(context.getRequest(), soapAction);
    }

    @Override
    public String extractResponseBody(@NonNull MessageContext context, @NonNull String soapAction, @Nullable Exception ex) {
        return extractResponseBody(context.getResponse(), soapAction, ex);
    }

    /**
     * Extracts message request body from given SOAP request.
     *
     * @param soapAction SOAP action, can be used to parametrize message body extraction
     */
    protected abstract String extractRequestBody(@NonNull WebServiceMessage request, @NonNull String soapAction);

    /**
     * Extracts message response body from given SOAP response.
     *
     * @param soapAction SOAP action, can be used to parametrize message body extraction
     * @param ex         exception thrown on handler execution, if any. Can be used for message body extraction
     *                   parametrization (see {@link ClientInterceptor#afterCompletion(MessageContext, Exception)} and
     *                   {@link EndpointInterceptor#afterCompletion(MessageContext, Object, Exception)})
     */
    protected abstract String extractResponseBody(@NonNull WebServiceMessage response, @NonNull String soapAction, @Nullable Exception ex);
}
