package cz.inqool.eas.eil.record.link;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class LinkStore extends DomainStore<Link, Link, QLink> {

    public LinkStore() {
        super(Link.class);
    }
}
