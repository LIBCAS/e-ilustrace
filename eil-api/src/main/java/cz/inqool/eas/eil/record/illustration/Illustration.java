package cz.inqool.eas.eil.record.illustration;

import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.eil.record.publication.PublicationEntry;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.time.Instant;

import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.eil.record.illustration.Illustration.*;
import static cz.inqool.eas.eil.record.illustration.Illustration.INDEXED;

@Viewable
@ViewableClass(views = {DETAIL, LIST, CREATE, UPDATE, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE}, generateRef = true)
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
@ViewableAnnotation(views = {DETAIL, LIST, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE}, value = {Entity.class, BatchSize.class, Table.class, DiscriminatorValue.class})
@Getter
@Setter
@Entity
@DiscriminatorValue(ILLUSTRATION)
public class Illustration extends Record {

    public static final String ILLUSTRATION = "ILLUSTRATION";
    public static final String XLSX = "XLSX";
    public static final String ESSENTIAL = "ESSENTIAL";
    public static final String MARC = "MARC";
    public static final String IDENTIFIER = "IDENTIFIER";
    public static final String EXHIBITION = "EXHIBITION";
    public static final String INDEXED = "INDEXED";

    @ViewableProperty(views = {DETAIL, IDENTIFIED, ESSENTIAL, EXHIBITION})
    @ViewableMapping(views = {DETAIL}, mappedTo = IDENTIFIED)
    @ViewableMapping(views = {EXHIBITION}, mappedTo = EXHIBITION)
    @ViewableMapping(views = {IDENTIFIED, ESSENTIAL}, useRef = true)
    @ManyToOne(fetch = FetchType.EAGER)
    Book book;

    /**
     * Údaje o tisku
     */
    @ViewableProperty(views = {DETAIL, LIST})
    @ViewableMapping(views = {DETAIL, LIST}, mappedTo = DEFAULT)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "print_entry_id")
    PublicationEntry printEntry;

    /**
     * Údaje o štočku
     */
    @ViewableProperty(views = {DETAIL})
    @ViewableMapping(views = {DETAIL}, mappedTo = DEFAULT)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "printing_plate_entry_id")
    PublicationEntry printingPlateEntry;

    /**
     * Defekt exempláře
     */
    @ViewableProperty(views = DETAIL)
    String defect;

    @ViewableProperty(views = {DETAIL, LIST, ESSENTIAL, MARC, EXHIBITION})
    @ViewableMapping(views = {DETAIL, ESSENTIAL}, mappedTo = DETAIL)
    @ViewableMapping(views = {LIST, EXHIBITION}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @OneToOne(cascade = CascadeType.ALL)
    @Where(clause = "deleted is null")
    File illustrationScan;

    @ViewableProperty(views = {DETAIL, LIST, ESSENTIAL, MARC, EXHIBITION})
    @ViewableMapping(views = {DETAIL, ESSENTIAL}, mappedTo = DETAIL)
    @ViewableMapping(views = {LIST, EXHIBITION}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "illustration_page_scan_id")
    @Where(clause = "deleted is null")
    File pageScan;

    @ViewableProperty(views = {DETAIL, LIST, XLSX, MARC, INDEXED})
    @Enumerated(EnumType.STRING)
    protected IconclassThemeState iconclassState = IconclassThemeState.UNENRICHED;

    @ViewableProperty(views = {DETAIL, LIST, XLSX, MARC, INDEXED})
    @Enumerated(EnumType.STRING)
    protected IconclassThemeState themeState = IconclassThemeState.UNENRICHED;

    @ViewableProperty(views = {DETAIL, LIST, ESSENTIAL, MARC, VISE})
    protected String viseFileId;

    @ViewableProperty(views = {ESSENTIAL, MARC})
    protected Instant viseIllScanCopied;

    @ViewableProperty(views = {DETAIL, LIST, ESSENTIAL, MARC, INDEXED, VISE})
    protected String cantaloupeIllScanId;

    @ViewableProperty(views = {ESSENTIAL, MARC})
    protected Instant cantaloupeIllScanCopied;

    @ViewableProperty(views = {DETAIL, LIST, ESSENTIAL, MARC, INDEXED})
    protected String cantaloupePageScanId;

    @ViewableProperty(views = {ESSENTIAL, MARC})
    protected Instant cantaloupePageScanCopied;

    @ViewableProperty(views = {DETAIL, LIST, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE})
    @Override
    public String getType() {
        return ILLUSTRATION;
    }
}
