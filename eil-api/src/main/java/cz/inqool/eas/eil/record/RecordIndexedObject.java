package cz.inqool.eas.eil.record;

import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.eil.author.record.RecordAuthorIndexedObject;
import cz.inqool.eas.eil.domain.EnumReference;
import cz.inqool.eas.eil.genre.GenreIndexedObject;
import cz.inqool.eas.eil.iconclass.IconclassCategoryIndexedObject;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceIndexedObject;
import cz.inqool.eas.eil.record.illustration.IconclassThemeState;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.eas.eil.record.illustration.IllustrationIndexed;
import cz.inqool.eas.eil.subject.entry.SubjectEntryIndexedObject;
import cz.inqool.eas.eil.subject.institution.SubjectInstitutionIndexedObject;
import cz.inqool.eas.eil.subject.person.SubjectPersonIndexedObject;
import cz.inqool.eas.eil.subject.place.SubjectPlaceIndexedObject;
import cz.inqool.eas.eil.theme.ThemeIndexedObject;
import cz.inqool.eas.eil.keyword.Keyword;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;
import static cz.inqool.eas.eil.record.illustration.Illustration.ILLUSTRATION;

@Getter
@org.springframework.data.elasticsearch.annotations.Document(indexName = "record")
@FieldNameConstants(innerTypeName = "IxFields")
public class RecordIndexedObject extends DatedIndexedObject<Record, RecordIndexed> {

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Integer)
    private int yearFrom;

    @Field(type = FieldType.Integer)
    private int yearTo;

    @Field(type = FieldType.Object)
    private RecordAuthorIndexedObject mainAuthor;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<RecordAuthorIndexedObject> coauthors = new LinkedHashSet<>();

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String title;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<SubjectPersonIndexedObject> subjectPersons = new LinkedHashSet<>();

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<SubjectInstitutionIndexedObject> subjectInstitutions = new LinkedHashSet<>();

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<SubjectEntryIndexedObject> subjectEntries = new LinkedHashSet<>();

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<SubjectPlaceIndexedObject> subjectPlaces = new LinkedHashSet<>();

    @Field(type = FieldType.Text)
    private Set<String> keywords = new LinkedHashSet<>();

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<GenreIndexedObject> genres = new LinkedHashSet<>();

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<PublishingPlaceIndexedObject> publishingPlaces = new LinkedHashSet<>();

    @Field(type = FieldType.Object, fielddata = true)
    private EnumReference<IconclassThemeState> iconclassState;

    @Field(type = FieldType.Object, fielddata = true)
    private EnumReference<IconclassThemeState> themeState;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<ThemeIndexedObject> themes;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<IconclassCategoryIndexedObject> iconclass;

    @Field(type = FieldType.Boolean)
    private boolean isIiif;

    @Override
    public void toIndexedObject(RecordIndexed obj) {
        super.toIndexedObject(obj);

        this.type = obj.getType().toLowerCase();
        this.identifier = obj.getIdentifier();
        this.yearFrom = obj.getYearFrom();
        this.yearTo = obj.getYearTo();
        this.mainAuthor = obj.getMainAuthor() == null ? null : RecordAuthorIndexedObject.of(obj.getMainAuthor());
        this.coauthors = obj.getAuthors().stream()
                .filter(a -> !a.isMainAuthor())
                .map(RecordAuthorIndexedObject::of)
                .collect(Collectors.toSet());
        this.title = obj.getTitle();
        this.subjectPersons = obj.getSubjectPersons().stream()
                .map(SubjectPersonIndexedObject::of)
                .collect(Collectors.toSet());
        this.subjectInstitutions = obj.getSubjectInstitutions().stream()
                .map(SubjectInstitutionIndexedObject::of)
                .collect(Collectors.toSet());
        this.subjectEntries = obj.getSubjectEntries().stream()
                .map(SubjectEntryIndexedObject::of)
                .collect(Collectors.toSet());
        this.subjectPlaces = obj.getSubjectPlaces().stream()
                .map(SubjectPlaceIndexedObject::of)
                .collect(Collectors.toSet());
        this.keywords = obj.getKeywords().stream()
                .map(Keyword::getLabel)
                .collect(Collectors.toSet());
        this.genres = obj.getGenres().stream()
                .map(GenreIndexedObject::of)
                .collect(Collectors.toSet());
        this.publishingPlaces = obj.getPublishingPlaces().stream()
                .map(PublishingPlaceIndexedObject::of)
                .collect(Collectors.toSet());
        if (obj.getType().equals(ILLUSTRATION)) {
            IllustrationIndexed ill = (IllustrationIndexed) obj;
            this.iconclassState = EnumReference.of(ill.getIconclassState());
            this.themeState = EnumReference.of(ill.getThemeState());
            this.isIiif = ((IllustrationIndexed) obj).getCantaloupeIllScanId() != null || ((IllustrationIndexed) obj).getCantaloupePageScanId() != null;
        }
        this.themes = obj.getThemes().stream()
                .map(ThemeIndexedObject::of)
                .collect(Collectors.toSet());
        this.iconclass = obj.getIconclass().stream()
                .map(IconclassCategoryIndexedObject::of)
                .collect(Collectors.toSet());
    }

    public static RecordIndexedObject of(RecordIndexed obj) {
        if (obj == null) {
            return null;
        }
        RecordIndexedObject indexed = new RecordIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }

    public static RecordIndexedObject of(Record obj) {
        return of(RecordIndexed.toView(obj));
    }
}
