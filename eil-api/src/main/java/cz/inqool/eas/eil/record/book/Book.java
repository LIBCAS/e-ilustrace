package cz.inqool.eas.eil.record.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.eil.record.publication.PublicationEntry;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Set;

import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.eil.record.book.Book.*;
import static cz.inqool.eas.eil.record.illustration.Illustration.INDEXED;

@Viewable
@ViewableClass(views = {DETAIL, LIST, CREATE, UPDATE, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE, FACET}, generateRef = true)
@ViewableMapping(views = DETAIL, mappedTo = DETAIL)
@ViewableMapping(views = LIST, mappedTo = LIST)
@ViewableMapping(views = CREATE, mappedTo = CREATE)
@ViewableMapping(views = UPDATE, mappedTo = UPDATE)
@ViewableMapping(views = XLSX, mappedTo = XLSX)
@ViewableMapping(views = ESSENTIAL, mappedTo = ESSENTIAL)
@ViewableMapping(views = MARC, mappedTo = MARC)
@ViewableMapping(views = IDENTIFIED, mappedTo = IDENTIFIED)
@ViewableMapping(views = IDENTIFIER, mappedTo = IDENTIFIER)
@ViewableMapping(views = EXHIBITION, mappedTo = EXHIBITION)
@ViewableMapping(views = INDEXED, mappedTo = INDEXED)
@ViewableMapping(views = SOURCES, mappedTo = SOURCES)
@ViewableMapping(views = VISE, mappedTo = VISE)
@ViewableMapping(views = FACET, mappedTo = FACET)
@ViewableAnnotation(views = {DETAIL, LIST, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE, FACET}, value = {Entity.class, BatchSize.class, Table.class, DiscriminatorValue.class})
@Getter
@Setter
@Entity
@DiscriminatorValue(BOOK)
public class Book extends Record {

    public static final String BOOK = "BOOK";
    public static final String XLSX = "XLSX";
    public static final String ESSENTIAL = "ESSENTIAL";
    public static final String MARC = "MARC";
    public static final String IDENTIFIER = "IDENTIFIER";
    public static final String EXHIBITION = "EXHIBITION";
    public static final String INDEXED = "INDEXED";

    @JsonIgnoreProperties({"book", "type"})
    @ViewableProperty(views = {DETAIL, LIST, IDENTIFIED})
    @ViewableMapping(views = {DETAIL, LIST}, mappedTo = ESSENTIAL)
    @ViewableMapping(views = {IDENTIFIED}, mappedTo = IDENTIFIED)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "book", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    Set<Illustration> illustrations;

    @ViewableProperty(views = {DETAIL, LIST})
    @ViewableMapping(views = {DETAIL, LIST}, mappedTo = DEFAULT)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "publishing_entry_id")
    PublicationEntry publishingEntry;

    @ViewableProperty(views = {DETAIL, LIST, ESSENTIAL, MARC})
    @ViewableMapping(views = {DETAIL, ESSENTIAL}, mappedTo = DETAIL)
    @ViewableMapping(views = {LIST}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "front_page_scan_id")
    @Where(clause = "deleted is null")
    File frontPageScan;

    @ViewableProperty(views = {DETAIL, LIST, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE, FACET})
    @Override
    public String getType() {
        return BOOK;
    }
}
