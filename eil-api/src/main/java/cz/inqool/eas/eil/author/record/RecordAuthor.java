package cz.inqool.eas.eil.author.record;

import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.eil.author.Author;
import cz.inqool.eas.eil.role.MarcRole;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.eil.author.record.RecordAuthor.FACET;

@Viewable
@DomainViews
@ViewableClass(views = {INDEXED, FACET}, generateRef = true)
@ViewableMapping(views = {INDEXED, FACET}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {INDEXED, FACET})
@Getter
@Setter
@Entity
@Table(name = "eil_record_author")
public class RecordAuthor extends DomainObject<RecordAuthor> {

    public static final String FACET = "FACET";

    @ViewableProperty(views = {DETAIL, LIST, INDEXED, FACET})
    @ViewableMapping(views = {DETAIL, INDEXED}, mappedTo = DETAIL)
    @ViewableMapping(views = LIST, mappedTo = LIST)
    @ViewableMapping(views = FACET, mappedTo = FACET)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    Author author;

    @ViewableProperty(views = {DETAIL, INDEXED, LIST, FACET})
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = MarcRole.class)
    @CollectionTable(name = "eil_record_author_role", joinColumns = @JoinColumn(name = "record_author_id"))
    @Column(name = "marc_role")
    @Enumerated(EnumType.STRING)
    Set<MarcRole> roles = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean isMainAuthor;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED, FACET})
    boolean fromBook;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED, FACET})
    boolean fromIllustration;
}
