package cz.inqool.eas.common.stomp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Configuration for web socket subsystem.
 *
 * If application wants to use web socket subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 * Note when behind apache reverse proxy, you need to proxy we protocol with separate proxy pass, or,
 * if on the same urls as regular http traffic, use conditional rewrite:
 *   RewriteEngine on
 *   RewriteCond %{HTTP:Upgrade} websocket [NC]
 *   RewriteCond %{HTTP:Connection} upgrade [NC]
 *   RewriteRule ^/?(.*) "ws://localhost:8080/$1" [P,L]
 * You also need to set headers (with mod headers):
 *   RequestHeader set X-Forwarded-Proto https
 *   RequestHeader set X-Forwarded-Port 443
 * and yml property:
 *   server.forward-headers-strategy: native
 * or you will fail origin checks on websocket init and end with 403.
 */
@Slf4j
@EnableWebSocketMessageBroker
public abstract class StompConfiguration extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {

    private TaskScheduler taskScheduler;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        SimpleBrokerRegistration simpleBroker = config.enableSimpleBroker("/topic", "/queue");
        if (taskScheduler != null) {
            simpleBroker
                    .setTaskScheduler(taskScheduler)
                    .setHeartbeatValue(new long[]{30_000, 30_000});
        }
    }

    @Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(getEndpoint());
    }

    /**
     * Configure STOMP endpoint
     *
     * @see #registerStompEndpoints(StompEndpointRegistry)
     */
    public abstract String getEndpoint();


    /**
     * @see DelegatingWebSocketMessageBrokerConfiguration#messageBrokerTaskScheduler()
     */
    @Autowired(required = false)
    public void setTaskScheduler(@Qualifier("messageBrokerTaskScheduler") TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }
}
