package cz.inqool.eas.common.ws.soap.logger;

import com.google.common.base.Charsets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.transport.TransportOutputStream;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.http.HttpUrlConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cz.inqool.eas.common.utils.CollectionUtils.join;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoapLoggerUtils {

    private static final Pattern SOAP_ACTION_PATTERN = Pattern.compile("\"?(?<soapAction>[^\"]*)\"?");
    private static final Pattern LOCAL_PART_PATTERN  = Pattern.compile(".*/(?<localPart>\\w+)");


    public static <T> T getContextProperty(@NonNull MessageContext context, @NonNull String propertyKey) {
        //noinspection unchecked
        return (T) context.getProperty(propertyKey);
    }

    /**
     * Extracts SOAP action from SOAP message
     */
    public static String extractSoapAction(@NonNull WebServiceMessage request) {
        if (request instanceof SaajSoapMessage) {
            SaajSoapMessage saajSoapMessage = (SaajSoapMessage) request;
            String soapAction = saajSoapMessage.getSoapAction();
            Matcher soapActionMatcher = SOAP_ACTION_PATTERN.matcher(soapAction);
            return soapActionMatcher.matches()
                    ? soapActionMatcher.group("soapAction")
                    : soapAction;
        }

        return null;
    }

    /**
     * Extracts local part of SOAP action from SOAP message
     */
    public static String extractLocalPart(@NonNull WebServiceMessage request) {
        String soapAction = extractSoapAction(request);
        return extractLocalPart(soapAction);
    }

    public static String extractLocalPart(@Nullable String soapAction) {
        if (soapAction == null) {
            return null;
        }

        Matcher localPartMatcher = LOCAL_PART_PATTERN.matcher(soapAction);
        return localPartMatcher.matches()
                ? localPartMatcher.group("localPart")
                : soapAction;
    }

    /**
     * Get namespace value of given endpoint or {@code null}, if no namespace is defined.
     *
     * @see PayloadRoot#namespace()
     */
    public static String getNamespace(@NonNull MethodEndpoint endpoint) {
        return getFromPayloadRoot(endpoint, PayloadRoot::namespace);
    }

    /**
     * Get local part value of given endpoint or {@code null}, if no local part is defined.
     *
     * @see PayloadRoot#localPart()
     */
    public static String getLocalPart(@NonNull MethodEndpoint endpoint) {
        return getFromPayloadRoot(endpoint, PayloadRoot::localPart);
    }

    /**
     * Get SOAP action of given endpoint
     *
     * @see PayloadRoot
     */
    public static String getSoapAction(@NonNull MethodEndpoint endpoint) {
        String namespace = getNamespace(endpoint);
        String localPart = getLocalPart(endpoint);

        return join("/", namespace, localPart);
    }

    private static <T> T getFromPayloadRoot(@NonNull MethodEndpoint endpoint, @NonNull Function<PayloadRoot, T> valueMapper) {
        return Optional.of(endpoint)
                .map(MethodEndpoint::getMethod)
                .map(method -> method.getAnnotation(PayloadRoot.class))
                .map(valueMapper)
                .orElse(null);
    }

    /**
     * Extracts message body from given SOAP message.
     */
    public static String extractSoapBody(@NotNull WebServiceMessage message) {
        try {
            ByteArrayTransportOutputStream stream = new ByteArrayTransportOutputStream();
            message.writeTo(stream);
            return stream.toString(Charsets.UTF_8);
        } catch (Throwable ex) {
            log.error("Failed to extract SOAP message body.", ex);
            return "error";
        }
    }

    /**
     * Return HTTP status (response code) from given transport context
     */
    public static Integer getResponseCode(@Nullable TransportContext context) {
        return Optional.ofNullable(context)
                .map(TransportContext::getConnection)
                .map(connection -> connection instanceof HttpUrlConnection
                        ? (HttpUrlConnection) connection
                        : null)
                .map(HttpUrlConnection::getConnection)
                .map(connection -> {
                    try {
                        return connection.getResponseCode();
                    } catch (IOException ignored) {
                        return null;
                    }
                })
                .orElse(null);
    }


    /**
     * Custom ByteArrayOutputStream to allow to be able to log also message headers.
     *
     * @see WebServiceMessage#writeTo(OutputStream)
     */
    private static class ByteArrayTransportOutputStream extends TransportOutputStream {

        private ByteArrayOutputStream baos;


        @Override
        protected ByteArrayOutputStream createOutputStream() {
            if (baos == null) {
                baos = new ByteArrayOutputStream();
            }
            return baos;
        }

        /**
         * Add message header as XML comment, so the resulting String is a valid XML
         *
         * @see TransportOutputStream#addHeader(String, String)
         */
        @Override
        public void addHeader(String name, String value) throws IOException {
            createOutputStream();
            String header = String.format("<!-- %s : %s -->\n", name, value);
            baos.write(header.getBytes());
        }

        public String toString(Charset charset) {
            return baos.toString(charset);
        }
    }
}
