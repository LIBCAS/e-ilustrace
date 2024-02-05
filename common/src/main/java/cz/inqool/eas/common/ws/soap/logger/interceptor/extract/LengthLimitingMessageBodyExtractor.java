package cz.inqool.eas.common.ws.soap.logger.interceptor.extract;

import cz.inqool.eas.common.ws.soap.logger.interceptor.extract.LengthLimitingMessageBodyExtractor.LimitingSettings.RequestLimitingSettings;
import cz.inqool.eas.common.ws.soap.logger.interceptor.extract.LengthLimitingMessageBodyExtractor.LimitingSettings.ResponseLimitingSettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static cz.inqool.eas.common.ws.soap.logger.SoapLoggerUtils.extractLocalPart;
import static cz.inqool.eas.common.ws.soap.logger.SoapLoggerUtils.extractSoapBody;
import static cz.inqool.eas.common.ws.soap.logger.interceptor.extract.LengthLimitingMessageBodyExtractor.ClippingStrategy.APPEND_OVER_LIMIT_CHARS_COUNT_ON_NEWLINE;

/**
 * Message body extractor that limits the length of extracted message based on configured settings.
 * <p>
 * Expose it as bean to be recognizable by logging interceptor factories.
 */
@RequiredArgsConstructor
public class LengthLimitingMessageBodyExtractor extends MessageBodyExtractorAdapter {

    /**
     * Default settings. Will be used when no custom settings in {@link #endpointSettings} are configured for an
     * endpoint.
     */
    @NonNull
    private final LimitingSettings defaultSettings;

    /**
     * Custom settings for specific endpoints. Takes precedence over {@link #defaultSettings}.
     * <p>
     * This map's key can be a {@code localPart} of an endpoint, or a string in format {@code <namespace>/<localPart>}
     * (see {@link PayloadRoot})
     */
    @NonNull
    private final Map<String, LimitingSettings> endpointSettings;

    private final Map<String, LimitingSettings> settingsCache = new HashMap<>();


    @Override
    public boolean isFor(@NonNull MessageContext context, @NonNull String soapAction) {
        return getSettings(soapAction).isEnabled();
    }

    @Override
    protected String extractRequestBody(@NonNull WebServiceMessage request, @NonNull String soapAction) {
        RequestLimitingSettings settings = getSettings(soapAction).getRequest();
        return clipMessageBody(request, settings);
    }

    @Override
    protected String extractResponseBody(@NonNull WebServiceMessage response, @NonNull String soapAction, @Nullable Exception ex) {
        ResponseLimitingSettings settings = getSettings(soapAction).getResponse();
        if (ex != null && settings.isNoClippingOnFailure()) {
            return extractSoapBody(response);
        } else {
            return clipMessageBody(response, settings);
        }
    }

    /**
     * Compute and cache limiting setting for given {@code soapAction}.
     * <p>
     * Settings can be configured only via {@code localPart} of endpoint, or with full endpoint path (in format:
     * {@code <namespace>/<localPart>}) to differentiate between different operations with same local parts.
     */
    private LimitingSettings getSettings(@Nullable String soapAction) {
        return settingsCache.computeIfAbsent(soapAction, action -> {
            if (action != null) {
                LimitingSettings settings = endpointSettings.get(soapAction);
                if (settings != null) {
                    return settings;
                }

                String localPart = extractLocalPart(soapAction);
                settings = endpointSettings.get(localPart);
                if (settings != null) {
                    return settings;
                }
            }

            return defaultSettings;
        });
    }

    /**
     * Extracts message body from given {@code message} using provided {@code settings} for message length limitation.
     */
    private String clipMessageBody(@NonNull WebServiceMessage message, @NonNull RequestLimitingSettings settings) {
        String messageBody = extractSoapBody(message);
        Integer lengthLimit = settings.getLengthLimit();
        ClippingStrategy clippingStrategy = settings.getClippingStrategy();

        return messageBody.length() > lengthLimit
                ? clippingStrategy.transformMessage(messageBody, lengthLimit)
                : messageBody;
    }


    /**
     * Settings for length limiting message body extractor.
     * <p>
     * This class can be used in {@link ConfigurationProperties} for configuration validation and clearer
     * specification.
     */
    @Getter
    @Setter
    public static class LimitingSettings {

        /**
         * Enable/disable length limiting message body extractor for this endpoint.
         */
        private boolean enabled = true;

        /**
         * Limiting settings for SOAP requests
         */
        @NotNull
        @Valid
        private RequestLimitingSettings request = new RequestLimitingSettings();

        /**
         * Limiting settings for SOAP responses
         */
        @NotNull
        @Valid
        private ResponseLimitingSettings response = new ResponseLimitingSettings();


        @Getter
        @Setter
        public static class RequestLimitingSettings {

            /**
             * Max length of extracted SOAP message body. Default is unlimited.
             */
            @NotNull
            protected Integer lengthLimit = Integer.MAX_VALUE;

            /**
             * Clipping strategy, used when SOAP message body is longer than specified {@link #lengthLimit}. Specifies
             * how to clip the message.
             */
            @NotNull
            protected ClippingStrategy clippingStrategy = APPEND_OVER_LIMIT_CHARS_COUNT_ON_NEWLINE;
        }


        @Getter
        @Setter
        public static class ResponseLimitingSettings extends RequestLimitingSettings {

            /**
             * No message clipping will be done when SOAP response handling fails
             */
            @NotNull
            private boolean noClippingOnFailure = true;
        }
    }


    /**
     * Clipping strategy, used when SOAP message body is longer than given limit. Specifies how to clip the message.
     */
    public enum ClippingStrategy {

        /**
         * Appends number of clipped characters in a new line at the end of the message.
         */
        APPEND_OVER_LIMIT_CHARS_COUNT_ON_NEWLINE {
            @Override
            public String transformMessage(@NonNull String message, int messageLengthLimit) {
                final int overLimit = message.length() - messageLengthLimit;
                return String.format("%s\n\tclipped %d characters...", message.substring(0, messageLengthLimit), overLimit);
            }
        };


        public abstract String transformMessage(@NonNull String message, int messageLengthLimit);
    }
}
