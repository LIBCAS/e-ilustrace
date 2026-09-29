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
import static cz.inqool.eas.eil.subject.entry.SubjectEntry.FACET;

/**
 * Předmětové heslo
 */
@Viewable
@DomainViews
@ViewableImplement(value = Authority.class, views = {DETAIL})
@ViewableClass(views = {INDEXED, FACET}, generateRef = true)
@ViewableMapping(views = {INDEXED, FACET}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {INDEXED, FACET})
@Getter
@Setter
@Entity
@Table(name = "eil_subject_entry")
public class SubjectEntry extends DatedObject<SubjectEntry> implements Authority {

    public static final String INDEXED = "indexed";
    public static final String FACET = "FACET";

    @ViewableProperty(views = {DETAIL, LIST, INDEXED, FACET})
    String label;

    @ViewableProperty(views = {DETAIL})
    String authorityCode;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED, FACET})
    boolean fromBook;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED, FACET})
    boolean fromIllustration;
}
