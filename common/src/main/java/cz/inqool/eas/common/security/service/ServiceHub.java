package cz.inqool.eas.common.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

@ConditionalOnProperty(prefix = "eas.session", name = "redis", havingValue = "true", matchIfMissing = true)
@Component
public class ServiceHub<S extends Session> {
    public static final String IMPERSONATING_SESSION_KEY = "IMPERSONATING_SESSION";

    private SessionRepository<S> sessionRepository;

    private HttpSession currentSession;

    private SessionCookieSerializer cookieSerializer;

    public RestTemplate templateWithSession(String sessionId) {
        RestTemplate template = new RestTemplate();

        template.getInterceptors().add((req, body, execution) -> {
            cookieSerializer.writeCookieToHeaders(sessionId, req.getHeaders());

            return execution.execute(req, body);
        });

        return template;
    }

    /**
     * Create new session for provided security context and store it in session repository
     *
     * @param securityContext Provided security context
     * @return new session
     */
    public S createNewSession(SecurityContext securityContext) {
        return createNewSession(securityContext, null);
    }

    /**
     * Create new session for provided security context and store it in session repository using specified session
     * interval.
     *
     * If the interval is negative, the session wont be invalidated.
     *
     * @param securityContext Provided security context
     * @return new session
     */
    public S createNewSession(SecurityContext securityContext, Duration maxInactiveInterval) {
        S session = sessionRepository.createSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        session.setAttribute(IMPERSONATING_SESSION_KEY, true);

        if (maxInactiveInterval != null) {
            session.setMaxInactiveInterval(maxInactiveInterval);
        }
        sessionRepository.save(session);

        return session;
    }

    /**
     * Removes given session from session repository
     *
     * @param session session to be removed
     */
    public void destroySession(Session session) {
        sessionRepository.deleteById(session.getId());
    }

    /**
     * Wraps function and provides {@link RestTemplate} which will have automatically added session cookie header based
     * on given session. The session is not destroyed on return. Suitable when multiple calls need to be executed with
     * the same session.
     * <p>
     * Example:
     * <pre>
     * public void callMultiple(SecurityContext securityContext) {
     *     Session session = createNewSession(securityContext);
     *     try {
     *         // i.e. do some polling
     *         for (int i=0; i < 10; i++) {
     *             doInSession(session, (template) -> {...});
     *         }
     *     } finally {
     *         destroySession(session);
     *     }
     * }
     * </pre>
     *
     * @param session  Provided security context
     * @param function Function to wrap
     * @param <T>      Type of return statement
     * @return return statement
     *
     * @see #createNewSession(SecurityContext)
     * @see #destroySession(Session)
     */
    public <T> T doInSession(Session session, Function<RestTemplate, T> function) {
        RestTemplate template = templateWithSession(session.getId());

        return function.apply(template);
    }

    /**
     * @see #doInSession(Session, Function)
     */
    public void doInSession(Session session, Consumer<RestTemplate> runnable) {
        doInSession(session, restTemplate -> {
            runnable.accept(restTemplate);
            return 0;
        });
    }

    /**
     * Wraps function and provides {@link RestTemplate} which will have automatically added session cookie header
     * based on new session created for provided security context.
     *
     * @param securityContext Provided security context
     * @param function Function to wrap
     * @param <T> Type of return statement
     * @return return statement
     */
    public <T> T doInContext(SecurityContext securityContext, Function<RestTemplate, T> function) {
        S session = createNewSession(securityContext);

        RestTemplate template = templateWithSession(session.getId());

        try {
            return function.apply(template);
        } finally {
            destroySession(session);
        }
    }

    /**
     * Wraps function and provides {@link RestTemplate} which will have automatically added session cookie header
     * based on current session.
     *
     * @param function Function to wrap
     * @param <T> Type of return statement
     * @return return statement
     */
    public <T> T doInCurrentContext(Function<RestTemplate, T> function) {
        String sessionId = currentSession.getId();
        RestTemplate template = templateWithSession(sessionId);

        return function.apply(template);
    }

    public void doInContext(SecurityContext securityContext, Consumer<RestTemplate> runnable) {
        doInContext(securityContext, (template) -> {
            runnable.accept(template);
            return 0;
        });
    }


    public void doInCurrentContext(Consumer<RestTemplate> runnable) {
        doInCurrentContext((template -> {
            runnable.accept(template);
            return 0;
        }));
    }


    @Autowired
    public void setSessionRepository(SessionRepository<S> sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Autowired
    public void setCookieSerializer(SessionCookieSerializer cookieSerializer) {
        this.cookieSerializer = cookieSerializer;
    }

    @Autowired(required = false)
    public void setCurrentSession(HttpSession currentSession) {
        this.currentSession = currentSession;
    }
}
