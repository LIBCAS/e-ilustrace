package cz.inqool.eas.common.exception.v2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for EAS exceptions
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("eas.exception")
public class ExceptionConfigurationProperties {

    private HandlerProperties handler = new HandlerProperties();


    @Getter
    @Setter
    public static class HandlerProperties {

        private RestProperties rest = new RestProperties();


        @Getter
        @Setter
        public static class RestProperties {

            /**
             * Specifies whether to use legacy REST exception handler ({@link cz.inqool.eas.common.exception.handler.RestExceptionHandler})
             * or the new REST exception handler ({@link cz.inqool.eas.common.exception.v2.rest.RestExceptionHandler}).
             * <p>
             * Defaults to {@code true}
             */
            private boolean legacy = true;

            /**
             * Specifies whether to send (serialize) debugInfo property in HTTP response.
             */
            private boolean sendDebugInfo = true;

            /**
             * Specifies whether to log client's IP address.
             */
            private boolean logIpAddress = false;

            /**
             * Log configuration
             */
            private LogProperties log = new LogProperties();


            @Getter
            @Setter
            public static class LogProperties {

                /**
                 * Default log strategy, should be used when no more specific strategy is provided.
                 */
                private LogStrategy defaultStrategy = LogStrategy.ALL;

                /**
                 * Custom log strategies for exceptions
                 */
                private Map<Class<? extends Throwable>, LogStrategy> strategies = new HashMap<>();
            }
        }
    }
}
