package cz.inqool.eas.eil.subject.entry;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.eil.authority.Authority;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Předmětové heslo
 */
@Viewable
@DomainViews
@ViewableImplement(value = Authority.class, views = {DETAIL})
@ViewableClass(views = {INDEXED}, generateRef = true)
@ViewableMapping(views = {INDEXED}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {INDEXED})
@Getter
@Setter
@Entity
@Table(name = "eil_subject_entry")
public class SubjectEntry extends DatedObject<SubjectEntry> implements Authority {

    public static final String INDEXED = "indexed";

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    String label;

    @ViewableProperty(views = {DETAIL})
    String authorityCode;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean fromBook;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean fromIllustration;
}
