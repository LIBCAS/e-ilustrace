package cz.inqool.eas.eil.security.token;

import cz.inqool.eas.eil.user.User;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TokenService {

    private static final long ONE_YEAR = 31536000L;

    private TokenStore store;
    @Value("${eil.security.token-expiration}")
    private long expiration;

    @Transactional
    public Token generateNewToken(@NonNull User user, boolean isForActivation) {
        Token token = new Token();
        token.setUser(user);
        if (isForActivation) {
            token.setExpiration(Instant.now().plusSeconds(ONE_YEAR));
        } else {
            token.setExpiration(Instant.now().plusSeconds(expiration));
        }
        return store.create(token);
    }

    @Transactional
    public boolean useToken(String id) {
        Token token = store.find(id);
        if (token != null && !token.isUsed() && token.getExpiration().isAfter(Instant.now())) {
            token.setUsed(true);
            store.update(token);
            return true;
        }
        return false;
    }

    public Token get(@NonNull String id) {
        return store.find(id);
    }

    @Autowired
    public void setPasswordTokenStore(TokenStore passwordTokenStore) {
        this.store = passwordTokenStore;
    }
}
