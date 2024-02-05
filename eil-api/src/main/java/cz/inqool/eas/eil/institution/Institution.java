package cz.inqool.eas.eil.institution;

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

@Viewable
@DomainViews
@ViewableImplement(value = Authority.class, views = {DETAIL})
@Getter
@Setter
@Entity
@Table(name = "eil_institution")
public class Institution extends DatedObject<Institution> implements Authority {

    @ViewableProperty(views = {DETAIL, LIST})
    String name;

    @ViewableProperty(views = {DETAIL, LIST})
    String authorityCode;
}
