package cz.inqool.eas.common.ws.soap.logger.interceptor.extract;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;

/**
 * Provides message body extraction functionality for web service client interceptors.
 */
public interface MessageBodyExtractor {

    /**
     * Returns {@code true} if this extractor is suitable for given SOAP message (represented with
     * {@code messageContext}) and respective {@code soapAction}), {@code false} otherwise.
     */
    boolean isFor(@NonNull MessageContext context, @NonNull String soapAction);

    /**
     * Extracts message request body from given SOAP message (represented with {@code messageContext}).
     *
     * @param soapAction SOAP action, can be used to parametrize message body extraction
     */
    String extractRequestBody(@NonNull MessageContext context, @NonNull String soapAction);

    /**
     * Extracts message response body from given SOAP message (represented with {@code messageContext}).
     *
     * @param soapAction SOAP action, can be used to parametrize message body extraction
     * @param ex         exception thrown on handler execution, if any. Can be used for message body extraction
     *                   parametrization (see {@link ClientInterceptor#afterCompletion(MessageContext, Exception)} and
     *                   {@link EndpointInterceptor#afterCompletion(MessageContext, Object, Exception)})
     */
    String extractResponseBody(@NonNull MessageContext context, @NonNull String soapAction, @Nullable Exception ex);
}
