package cz.inqool.eas.eil.subject.person;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.eil.authority.Authority;
import cz.inqool.eas.eil.person.Person;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Osoba - předmět díla
 */
@Viewable
@DomainViews
@ViewableImplement(value = Authority.class, views = {DETAIL, LIST})
@ViewableImplement(value = Person.class, views = {DETAIL})
@ViewableClass(views = {INDEXED}, generateRef = true)
@ViewableMapping(views = {INDEXED}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {INDEXED})
@Getter
@Setter
@Entity
@Table(name = "eil_subject_person")
public class SubjectPerson extends DatedObject<SubjectPerson> implements Person, Authority {

    public static final String INDEXED = "indexed";

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    String fullName;

    @ViewableProperty(views = {DETAIL, INDEXED})
    String birthYear;

    @ViewableProperty(views = {DETAIL, INDEXED})
    String deathYear;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    String authorityCode;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean fromBook;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean fromIllustration;
}
