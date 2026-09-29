package cz.inqool.eas.common.client;

import cz.inqool.eas.common.security.User;
import cz.inqool.eas.common.security.UserBuilder;
import cz.inqool.eas.common.security.service.ServiceHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.Session;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Configuration for inter service communication subsystem.
 *
 * If application wants to use this subsystem,
 * it needs to extend this class and add {@link Configuration} annotation
 * and implement the `configure` method.
 *
 */
public abstract class ClientConfiguration {
    private ServiceHub<? extends Session> serviceHub;

    @Bean
    public Supplier<Session> clientSession() {
        return () -> serviceHub.createNewSession(getInternalContext(), Duration.ofSeconds(-1));
    }

    protected SecurityContext getInternalContext() {
        UserBuilder userBuilder = UserBuilder.anUser();
        this.configure(userBuilder);

        User user = userBuilder.build();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        context.setAuthentication(authentication);

        return context;
    }

    @Autowired
    public void setServiceHub(ServiceHub<? extends Session> serviceHub) {
        this.serviceHub = serviceHub;
    }

    /**
     * Method for configuration the client {@link User}.
     *
     * @param builder Builder used for configuration
     */
    protected abstract void configure(UserBuilder builder);
}
