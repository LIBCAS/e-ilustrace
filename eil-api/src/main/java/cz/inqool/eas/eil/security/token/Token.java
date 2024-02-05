package cz.inqool.eas.eil.security.token;

import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.eil.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "eil_token")
@BatchSize(size = 100)
public class Token extends DomainObject<Token> {

    @Fetch(FetchMode.SELECT)
    @ManyToOne
    private User user;

    private Instant expiration;

    private boolean used;

}
