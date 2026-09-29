package cz.inqool.eas.eil.genre;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.eil.authority.Authority;
import cz.inqool.entityviews.Viewable;
import cz.inqool.entityviews.ViewableImplement;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

import static cz.inqool.eas.common.domain.DomainViews.DETAIL;
import static cz.inqool.eas.common.domain.DomainViews.LIST;

/**
 * Žánr / forma
 */
@Viewable
@DomainViews
@ViewableImplement(value = Authority.class, views = {DETAIL})
@Getter
@Setter
@Entity
@Table(name = "eil_genre")
public class Genre extends DatedObject<Genre> implements Authority {

    @ViewableProperty(views = {DETAIL, LIST})
    String name;

    @ViewableProperty(views = {DETAIL})
    String authorityCode;
}
