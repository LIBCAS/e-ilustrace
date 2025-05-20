package cz.inqool.eas.eil.record.dto;

import cz.inqool.eas.eil.author.AuthorFacet;
import cz.inqool.eas.eil.genre.Genre;
import cz.inqool.eas.eil.keyword.Keyword;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceFacet;
import cz.inqool.eas.eil.subject.entry.SubjectEntryFacet;
import cz.inqool.eas.eil.subject.person.SubjectPersonFacet;
import cz.inqool.eas.eil.subject.place.SubjectPlaceFacet;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RecordFacetsDto {
    private Set<AuthorFacet> authors = new HashSet<>();
    private Set<PublishingPlaceFacet> publishingPlaces = new HashSet<>();
    private Set<SubjectPlaceFacet> subjectPlaces = new HashSet<>();
    private Set<SubjectPersonFacet> subjectPersons = new HashSet<>();
    private Set<SubjectEntryFacet> subjectEntries = new HashSet<>();
    private Set<Keyword> keywords = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
}
