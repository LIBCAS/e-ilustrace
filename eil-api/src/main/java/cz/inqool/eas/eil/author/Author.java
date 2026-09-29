package cz.inqool.eas.eil.author;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.eil.authority.Authority;
import cz.inqool.eas.eil.person.Person;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;

import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.eil.author.record.RecordAuthor.FACET;

@Viewable
@DomainViews
@ViewableClass(views = {FACET})
@ViewableMapping(views = {FACET}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {FACET})
@ViewableImplement(value = Authority.class, views = {DETAIL})
@ViewableImplement(value = Person.class, views = {DETAIL})
@Getter
@Setter
@Entity
@Table(name = "eil_author")
public class Author extends DatedObject<Author> implements Person, Authority {

    public static final String FACET = "FACET";

    @ViewableProperty(views = {DETAIL, LIST, FACET})
    String fullName;

    @ViewableProperty(views = {DETAIL})
    String birthYear;

    @ViewableProperty(views = {DETAIL})
    String deathYear;

    @ViewableProperty(views = {DETAIL, LIST})
    String authorityCode;
}
