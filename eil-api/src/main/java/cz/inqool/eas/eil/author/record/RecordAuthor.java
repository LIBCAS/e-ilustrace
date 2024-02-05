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

@Viewable
@DomainViews
@ViewableClass(views = {INDEXED}, generateRef = true)
@ViewableMapping(views = {INDEXED}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {INDEXED})
@Getter
@Setter
@Entity
@Table(name = "eil_record_author")
public class RecordAuthor extends DomainObject<RecordAuthor> {

    public static final String INDEXED = "indexed";

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    @ViewableMapping(views = {DETAIL, INDEXED}, mappedTo = DETAIL)
    @ViewableMapping(views = LIST, mappedTo = LIST)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    Author author;

    @ViewableProperty(views = {DETAIL, INDEXED, LIST})
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = MarcRole.class)
    @CollectionTable(name = "eil_record_author_role", joinColumns = @JoinColumn(name = "record_author_id"))
    @Column(name = "marc_role")
    @Enumerated(EnumType.STRING)
    Set<MarcRole> roles = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean isMainAuthor;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean fromBook;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean fromIllustration;
}
