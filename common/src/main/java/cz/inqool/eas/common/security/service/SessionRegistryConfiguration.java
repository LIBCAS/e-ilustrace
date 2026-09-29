package cz.inqool.eas.common.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FlushMode;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@ConditionalOnProperty(prefix = "eas.session", name = "redis", havingValue = "true", matchIfMissing = true)
@Configuration
public class SessionRegistryConfiguration {
    private int timeout;

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    public SpringSessionBackedSessionRegistry sessionRegistry(RedisIndexedSessionRepository redisIndexedSessionRepository) {
        redisIndexedSessionRepository.setDefaultMaxInactiveInterval(timeout);
        redisIndexedSessionRepository.setFlushMode(FlushMode.IMMEDIATE);
        return new SpringSessionBackedSessionRegistry(redisIndexedSessionRepository);
    }

    @Bean
    public SessionCookieSerializer cookieSerializer(ServerProperties serverProperties) {
        Session.Cookie cookie = serverProperties.getServlet().getSession().getCookie();
        SessionCookieSerializer cookieSerializer = new SessionCookieSerializer();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(cookie::getName).to(cookieSerializer::setCookieName);
        map.from(cookie::getDomain).to(cookieSerializer::setDomainName);
        map.from(cookie::getPath).to(cookieSerializer::setCookiePath);
        map.from(cookie::getHttpOnly).to(cookieSerializer::setUseHttpOnlyCookie);
        map.from(cookie::getSecure).to(cookieSerializer::setUseSecureCookie);
        map.from(cookie::getMaxAge).to((maxAge) -> cookieSerializer.setCookieMaxAge((int) maxAge.getSeconds()));
        return cookieSerializer;
    }

    @Autowired
    public void setTimeout(@Value("${eas.session.timeout:1800}") int timeout) {
        this.timeout = timeout;
    }
}
