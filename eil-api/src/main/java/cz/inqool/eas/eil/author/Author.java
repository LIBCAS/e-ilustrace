package cz.inqool.eas.eil.author;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.eil.authority.Authority;
import cz.inqool.eas.eil.person.Person;
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
@ViewableImplement(value = Person.class, views = {DETAIL})
@Getter
@Setter
@Entity
@Table(name = "eil_author")
public class Author extends DatedObject<Author> implements Person, Authority {

    @ViewableProperty(views = {DETAIL, LIST})
    String fullName;

    @ViewableProperty(views = {DETAIL})
    String birthYear;

    @ViewableProperty(views = {DETAIL})
    String deathYear;

    @ViewableProperty(views = {DETAIL, LIST})
    String authorityCode;
}
