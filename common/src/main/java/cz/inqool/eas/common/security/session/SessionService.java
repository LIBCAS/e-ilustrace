package cz.inqool.eas.common.security.session;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;
import static cz.inqool.eas.common.utils.AssertionUtils.ifPresentDo;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * Service allowing convenient work with sessions stored in redis repository.
 * <p>
 * This class is marked as {@link Beta} because of {@link #getActiveAuthenticationStream()}.
 */
@Beta
@Slf4j
@Service
@ConditionalOnBean(RedisIndexedSessionRepository.class)
public class SessionService {

    protected RedisIndexedSessionRepository sessionRepository;


    /**
     * Applies given {@code collector} function on all active sessions, allowing to transform / process them to some
     * different format.
     */
    public <R> R mapActiveSessions(Function<Stream<Authentication>, R> collector) {
        return ifPresentDo(getActiveAuthenticationStream(), collector);
    }

    /**
     * Applies given {@code consumer} function on all active sessions, allowing to do something with all active
     * sessions.
     */
    public void doWithActiveSessions(Consumer<Stream<Authentication>> consumer) {
        ifPresent(getActiveAuthenticationStream(), consumer);
    }

    /**
     * Applies given {@code collector} function on all active sessions, allowing to transform / process them to some
     * different format.
     * <p>
     * Note that unlike {@link #mapActiveSessions(Function)} this method assumes that in redis there are only sessions
     * represented by {@link User}. Other types are silently ignored.
     * <p>
     * Usage example, code below returns ID of all logged-in users:
     * <pre>
     *     mapActiveUsers(SessionCollectors.ACTIVE_USER_IDS_COLLECTOR);
     * </pre>
     */
    public <R> R mapActiveUsers(Function<Stream<User>, R> collector) {
        Function<Stream<Authentication>, R> authenticationCollector = authenticationStream -> {
            Stream<User> contextUserStream = authenticationStream.map(Authentication::getPrincipal)
                    .filter(principal -> principal instanceof User)
                    .map(principal -> (User) principal);

            return collector.apply(contextUserStream);
        };

        return mapActiveSessions(authenticationCollector);
    }

    /**
     * Applies given {@code consumer} function on all active sessions, allowing to do something with all active
     * sessions.
     * <p>
     * Note that unlike {@link #doWithActiveSessions(Consumer)} this method assumes that in redis there are only
     * sessions represented by {@link User}. Other types are silently ignored.
     */
    public void doWithActiveUsers(Consumer<Stream<User>> consumer) {
        Consumer<Stream<Authentication>> authenticationCollector = authenticationStream -> {
            Stream<User> contextUserStream = authenticationStream.map(Authentication::getPrincipal)
                    .filter(principal -> principal instanceof User)
                    .map(principal -> (User) principal);

            consumer.accept(contextUserStream);
        };

        doWithActiveSessions(authenticationCollector);
    }

    /**
     * Returns stream of all active sessions in redis.
     * <p>
     * This method is marked as {@link Beta}, because it was fully tested only on one specific use-case. Filter keys may
     * be incomplete.
     */
    @Beta
    private Stream<Authentication> getActiveAuthenticationStream() {
        RedisOperations<Object, Object> sessionRedisOperations = sessionRepository.getSessionRedisOperations();
        Set<Object> sessionKeys = sessionRedisOperations.keys("spring:session:sessions:[^expires]*");
        if (sessionKeys == null) {
            return null;
        }

        return sessionKeys.stream()
                .map(sessionKey -> (String) sessionKey)
                .map(sessionKey -> sessionKey.replace("spring:session:sessions:", ""))
                .map(sessionId -> (Session) sessionRepository.findById(sessionId))
                .filter(Objects::nonNull)
                .map(session -> session.getAttribute(SPRING_SECURITY_CONTEXT_KEY))
                .filter(Objects::nonNull)
                .map(securityContext -> (SecurityContextImpl) securityContext)
                .map(SecurityContextImpl::getAuthentication);
    }


    @Autowired
    public void setSessionRepository(RedisIndexedSessionRepository repository) {
        this.sessionRepository = repository;
    }


    /**
     * Useful collectors for {@link SessionService} methods
     */
    public static class SessionCollectors {

        /**
         * Used along with {@link SessionService#mapActiveUsers(Function)} to return IDs of all active users.
         */
        public static final Function<Stream<User>, Set<String>> ACTIVE_USER_IDS_COLLECTOR = userStream -> userStream
                .map(User::getId)
                .collect(Collectors.toSet());

        /**
         * Used along with {@link SessionService#mapActiveUsers(Function)} to return all active users.
         */
        public static final Function<Stream<User>, Set<User>> ACTIVE_USERS_COLLECTOR = userStream -> userStream
                .collect(Collectors.toSet());
    }
}
