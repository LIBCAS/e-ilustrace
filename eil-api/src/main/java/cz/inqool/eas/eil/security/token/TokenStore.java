package cz.inqool.eas.eil.security.token;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class TokenStore extends DomainStore<Token, Token, QToken> {

    public TokenStore() {
        super(Token.class);
    }
}
