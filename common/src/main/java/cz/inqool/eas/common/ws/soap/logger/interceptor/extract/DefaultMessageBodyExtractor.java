package cz.inqool.eas.common.ws.soap.logger.interceptor.extract;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;

import static cz.inqool.eas.common.ws.soap.logger.SoapLoggerUtils.extractSoapBody;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

/**
 * Default message body extractor for client/server interceptors. Extracts all content from SOAP message body, without
 * any transformation / modification.
 * <p>
 * Always registered as bean (unconditionally) with the lowest precedence, so at least one extractor is present.
 */
@SuppressWarnings("DefaultAnnotationParam")
@Slf4j
@Component
@Order(LOWEST_PRECEDENCE)
public class DefaultMessageBodyExtractor extends MessageBodyExtractorAdapter {

    @Override
    public boolean isFor(@NotNull MessageContext context, @NonNull String soapAction) {
        return true;
    }

    @Override
    protected String extractRequestBody(@NotNull WebServiceMessage request, @NonNull String soapAction) {
        return extractSoapBody(request);
    }

    @Override
    protected String extractResponseBody(@NotNull WebServiceMessage response, @NonNull String soapAction, @Nullable Exception ex) {
        return extractSoapBody(response);
    }
}
