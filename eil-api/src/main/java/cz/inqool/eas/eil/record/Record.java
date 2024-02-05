package cz.inqool.eas.eil.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.eil.author.record.RecordAuthor;
import cz.inqool.eas.eil.genre.Genre;
import cz.inqool.eas.eil.iconclass.IconclassCategory;
import cz.inqool.eas.eil.institution.record.RecordInstitution;
import cz.inqool.eas.eil.keyword.Keyword;
import cz.inqool.eas.eil.record.link.Link;
import cz.inqool.eas.eil.record.note.Note;
import cz.inqool.eas.eil.record.owner.Owner;
import cz.inqool.eas.eil.publishingplace.PublishingPlace;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.eas.eil.record.reference.Reference;
import cz.inqool.eas.eil.subject.entry.SubjectEntry;
import cz.inqool.eas.eil.subject.institution.SubjectInstitution;
import cz.inqool.eas.eil.subject.person.SubjectPerson;
import cz.inqool.eas.eil.subject.place.SubjectPlace;
import cz.inqool.eas.eil.theme.Theme;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.eil.record.Record.*;
import static cz.inqool.eas.eil.record.Record.INDEXED;
import static cz.inqool.eas.eil.record.Record.VISE;
import static cz.inqool.eas.eil.record.book.Book.BOOK;
import static cz.inqool.eas.eil.record.book.Book.IDENTIFIER;
import static cz.inqool.eas.eil.record.book.Book.MARC;
import static cz.inqool.eas.eil.record.illustration.Illustration.ILLUSTRATION;
import static javax.persistence.CascadeType.*;

@Viewable
@ViewableClass(views = {DETAIL, LIST, CREATE, UPDATE, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE})
@ViewableMapping(views = {DETAIL, LIST, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE}, mappedTo = DEFAULT)
@ViewableMapping(views = CREATE, mappedTo = CREATE)
@ViewableMapping(views = UPDATE, mappedTo = UPDATE)
@ViewableAnnotation(views = {DETAIL, LIST, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE}, value = {Entity.class, BatchSize.class, Table.class})
@ViewableLeaf(subClasses = {
        Illustration.class,
        Book.class
})
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "eil_record")
@JsonTypeInfo(
        use = NAME,
        include = EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = ILLUSTRATION, value = Illustration.class),
        @JsonSubTypes.Type(name = BOOK, value = Book.class)
})
public abstract class Record extends DatedObject<Record> {

    public static final String XLSX = "XLSX";
    public static final String ESSENTIAL = "ESSENTIAL";
    public static final String MARC = "MARC";
    public static final String IDENTIFIER = "IDENTIFIER";
    public static final String EXHIBITION = "EXHIBITION";
    public static final String INDEXED = "INDEXED";
    public static final String SOURCES = "SOURCES";
    public static final String VISE = "VISE";

    @ViewableProperty(views = {DETAIL, LIST, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, VISE})
    String identifier;

    @ViewableProperty(views = {DETAIL, LIST, ESSENTIAL, EXHIBITION, INDEXED, VISE})
    int yearFrom;

    @ViewableProperty(views = {DETAIL, LIST, ESSENTIAL, EXHIBITION, INDEXED, VISE})
    int yearTo;

    @ViewableProperty(views = {DETAIL, IDENTIFIED})
    String fixedLengthField;

    @JsonIgnore
    @ViewableProperty(views = {DETAIL, LIST, EXHIBITION, INDEXED, SOURCES, VISE})
    @ViewableMapping(views = {DETAIL, LIST, EXHIBITION, VISE}, mappedTo = DETAIL)
    @ViewableMapping(views = {INDEXED}, mappedTo = INDEXED)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "record_id")
    Set<RecordAuthor> authors = new LinkedHashSet<>();

    @JsonIgnore
    @ViewableProperty(views = {DETAIL, LIST})
    @ViewableMapping(views = {DETAIL, LIST}, mappedTo = DEFAULT)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "record_id")
    Set<RecordInstitution> institutions = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, LIST, IDENTIFIED, ESSENTIAL, EXHIBITION, INDEXED})
    String title;

    @ViewableProperty(views = {DETAIL})
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "eil_record_variant_title", joinColumns = @JoinColumn(name = "record_id"))
    @Column(name = "variant_title")
    Set<String> variantTitles = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, ESSENTIAL})
    String physicalDescription;

    @ViewableProperty(views = {DETAIL})
    String technique;

    @ViewableProperty(views = {DETAIL})
    String dimensions;

    @ViewableProperty(views = {DETAIL})
    @ViewableMapping(views = {DETAIL}, mappedTo = DEFAULT)
    @OneToMany(fetch = FetchType.EAGER, cascade =  CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinColumn(name = "record_id")
    Set<Note> notes = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL})
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "eil_record_content_note", joinColumns = @JoinColumn(name = "record_id"))
    @Column(name = "content_note")
    Set<String> contentNotes = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL})
    @ViewableMapping(views = {DETAIL}, mappedTo = DEFAULT)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinColumn(name = "record_id")
    Set<Reference> references = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, INDEXED, SOURCES})
    @ViewableMapping(views = {DETAIL}, mappedTo = DETAIL)
    @ViewableMapping(views = {INDEXED}, mappedTo = INDEXED)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinTable(name = "eil_record_subject_person",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_person_id"))
    Set<SubjectPerson> subjectPersons = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, INDEXED})
    @ViewableMapping(views = {DETAIL}, mappedTo = DETAIL)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinTable(name = "eil_record_subject_institution",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_institution_id"))
    Set<SubjectInstitution> subjectInstitutions = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, INDEXED, SOURCES})
    @ViewableMapping(views = {DETAIL}, mappedTo = DETAIL)
    @ViewableMapping(views = {INDEXED}, mappedTo = INDEXED)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinTable(name = "eil_record_subject_entry",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_entry_id"))
    Set<SubjectEntry> subjectEntries = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, INDEXED, SOURCES})
    @ViewableMapping(views = {DETAIL}, mappedTo = DETAIL)
    @ViewableMapping(views = {INDEXED}, mappedTo = INDEXED)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinTable(name = "eil_record_subject_place",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_place_id"))
    Set<SubjectPlace> subjectPlaces = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, MARC, INDEXED})
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinTable(name = "eil_record_keyword",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    Set<Keyword> keywords = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, INDEXED})
    @ViewableMapping(views = {DETAIL}, mappedTo = DETAIL)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinTable(name = "eil_record_genre",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    Set<Genre> genres = new LinkedHashSet<>();

    @JsonIgnoreProperties("record")
    @ViewableProperty(views = {DETAIL, MARC})
    @ViewableMapping(views = {DETAIL}, mappedTo = DEFAULT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    Set<Link> links = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL})
    @ViewableMapping(views = {DETAIL}, mappedTo = DEFAULT)
    @OneToMany(fetch = FetchType.EAGER, cascade =  CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinColumn(name = "record_id")
    Set<Owner> owners = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, INDEXED, SOURCES, VISE})
    @ViewableMapping(views = {DETAIL, VISE}, mappedTo = DETAIL)
    @ViewableMapping(views = {INDEXED}, mappedTo = INDEXED)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinTable(name = "eil_record_publishing_place",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "publishing_place_id"))
    Set<PublishingPlace> publishingPlaces = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, UPDATE, XLSX, MARC, INDEXED})
    @ViewableMapping(views = {DETAIL}, mappedTo = DEFAULT)
    @ViewableMapping(views = {UPDATE, XLSX}, useRef = true)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "eil_record_iconclass_category",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "iconclass_cat_id"))
    Set<IconclassCategory> iconclass = new LinkedHashSet<>();

    @ViewableProperty(views = {DETAIL, UPDATE, XLSX, MARC, INDEXED})
    @ViewableMapping(views = {DETAIL}, mappedTo = DEFAULT)
    @ViewableMapping(views = {UPDATE, XLSX}, useRef = true)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "eil_record_theme",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id"))
    Set<Theme> themes = new LinkedHashSet<>();

    @JsonIgnoreProperties("mainAuthor")
    @ViewableProperty(views = {DETAIL, LIST, EXHIBITION, INDEXED, VISE})
    @ViewableMapping(views = {DETAIL, LIST, EXHIBITION, VISE}, mappedTo = DETAIL)
    @ViewableMapping(views = {INDEXED}, mappedTo = INDEXED)
    public RecordAuthor getMainAuthor() {
        return authors.stream().filter(a -> a.isMainAuthor()).findFirst().orElse(null);
    }

    @JsonIgnoreProperties("mainAuthor")
    @ViewableProperty(views = {DETAIL, VISE})
    @ViewableMapping(views = {DETAIL, VISE}, mappedTo = DETAIL)
    public Set<RecordAuthor> getCoauthors() {
        return authors.stream().filter(a -> !a.isMainAuthor()).collect(Collectors.toSet());
    }

    @JsonIgnoreProperties("mainWorkshop")
    @ViewableProperty(views = {DETAIL, LIST})
    @ViewableMapping(views = {DETAIL, LIST}, mappedTo = DEFAULT)
    public RecordInstitution getMainWorkshop() {
        return institutions.stream().filter(i -> i.isMainWorkshop()).findFirst().orElse(null);
    }

    @JsonIgnoreProperties("mainWorkshop")
    @ViewableProperty(views = DETAIL)
    @ViewableMapping(views = DETAIL, mappedTo = DEFAULT)
    public Set<RecordInstitution> getCoinstitutions() {
        return institutions.stream().filter(i -> !i.isMainWorkshop()).collect(Collectors.toSet());
    }

    @ViewableProperty(views = {DETAIL, LIST, IDENTIFIED, XLSX, ESSENTIAL, MARC, IDENTIFIER, EXHIBITION, INDEXED, SOURCES, VISE})
    public abstract String getType();
}
