package cz.inqool.eas.eil.record.publication;

import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.entityviews.Viewable;
import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static cz.inqool.eas.common.domain.DomainViews.DEFAULT;

@Viewable
@ViewableClass(views = {DEFAULT})
@ViewableMapping(views = {DEFAULT}, mappedTo = DEFAULT)
@Getter
@Setter
@Entity
@Table(name = "eil_publication_entry")
public class PublicationEntry extends DomainObject<PublicationEntry> {

    @ViewableProperty(views = {DEFAULT})
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "eil_publication_entry_place", joinColumns = @JoinColumn(name = "publication_entry_id"))
    @Column(name = "place_of_publication")
    List<String> placesOfPublication = new ArrayList<>();

    @ViewableProperty(views = {DEFAULT})
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "eil_publication_entry_originator", joinColumns = @JoinColumn(name = "publication_entry_id"))
    @Column(name = "originator")
    List<String> originators = new ArrayList<>();

    // Datum ukladano pouze jako String pro zachovani struktury importovanych dat, pro vyhledavani podle data vydani
    // se pouzivaji pole yearFrom a yearTo v Recordu
    @ViewableProperty(views = {DEFAULT})
    String date;
}
