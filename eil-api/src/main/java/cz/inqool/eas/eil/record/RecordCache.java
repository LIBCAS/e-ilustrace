package cz.inqool.eas.eil.record;

import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.eil.author.AuthorFacet;
import cz.inqool.eas.eil.author.record.RecordAuthorFacet;
import cz.inqool.eas.eil.genre.Genre;
import cz.inqool.eas.eil.keyword.Keyword;
import cz.inqool.eas.eil.publishingplace.PublishingPlaceFacet;
import cz.inqool.eas.eil.record.dto.RecordFacetsDto;
import cz.inqool.eas.eil.role.MarcRole;
import cz.inqool.eas.eil.subject.entry.SubjectEntryFacet;
import cz.inqool.eas.eil.subject.person.SubjectPersonFacet;
import cz.inqool.eas.eil.subject.place.SubjectPlaceFacet;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Getter
public class RecordCache {

    private RecordFacetsDto bookCache = new RecordFacetsDto();
    private RecordFacetsDto illCache = new RecordFacetsDto();

    private Instant bookReload;
    private Instant illReload;

    private RecordRepository recordRepository;

    public void reloadBooks(Instant now, Params params) {
        if (this.bookReload == null || this.bookReload.isBefore(now.minus(6, ChronoUnit.HOURS))) {
            this.bookReload = now;
            this.bookCache = getBooks(params);
        }
    }

    public void reloadIlls(Instant now, Params params) {
        if (this.illReload == null || this.illReload.isBefore(now.minus(6, ChronoUnit.HOURS))) {
            this.illReload = now;
            this.illCache = getIlls(params);
        }
    }

    public RecordFacetsDto getBooks(Params params) {
        Result<RecordFacet> records = recordRepository.listByParams(RecordFacet.class, params);
        RecordFacetsDto result = new RecordFacetsDto();
        List<MarcRole> roles = List.of(MarcRole.AUTHOR, MarcRole.PRINTER, MarcRole.PUBLISHER, MarcRole.ILLUSTRATOR, MarcRole.WOODCARVER);
        for (RecordFacet record : records.getItems()) {
            Set<AuthorFacet> authors = record.getAuthors().stream()
                    .filter(a -> a.isFromBook() && !Collections.disjoint(a.getRoles(), roles)).map(RecordAuthorFacet::getAuthor)
                    .collect(Collectors.toSet());
            result.getAuthors().addAll(authors);

            Set<PublishingPlaceFacet> publishingPlaces = record.getPublishingPlaces().stream().filter(PublishingPlaceFacet::isFromBook).collect(Collectors.toSet());
            result.getPublishingPlaces().addAll(publishingPlaces);

            Set<SubjectPlaceFacet> subjectPlaces = record.getSubjectPlaces().stream().filter(SubjectPlaceFacet::isFromBook).collect(Collectors.toSet());
            result.getSubjectPlaces().addAll(subjectPlaces);

            Set<SubjectPersonFacet> subjectPeople = record.getSubjectPersons().stream().filter(SubjectPersonFacet::isFromBook).collect(Collectors.toSet());
            result.getSubjectPersons().addAll(subjectPeople);

            Set<SubjectEntryFacet> subjectEntries = record.getSubjectEntries().stream().filter(SubjectEntryFacet::isFromBook).collect(Collectors.toSet());
            result.getSubjectEntries().addAll(subjectEntries);

            Set<Keyword> keywords = new HashSet<>(record.getKeywords());
            result.getKeywords().addAll(keywords);

            Set<Genre> genres = new HashSet<>(record.getGenres());
            result.getGenres().addAll(genres);
        }
        return result;
    }

    public RecordFacetsDto getIlls(Params params) {
        Result<RecordFacet> records = recordRepository.listByParams(RecordFacet.class, params);
        RecordFacetsDto result = new RecordFacetsDto();
        List<MarcRole> roles = List.of(MarcRole.AUTHOR, MarcRole.PRINTER, MarcRole.PUBLISHER, MarcRole.ILLUSTRATOR, MarcRole.WOODCARVER);
        for (RecordFacet record : records.getItems()) {
            Set<AuthorFacet> authors = record.getAuthors().stream()
                    .filter(a -> a.isFromIllustration() && !Collections.disjoint(a.getRoles(), roles)).map(RecordAuthorFacet::getAuthor)
                    .collect(Collectors.toSet());
            result.getAuthors().addAll(authors);

            Set<PublishingPlaceFacet> publishingPlaces = record.getPublishingPlaces().stream().filter(PublishingPlaceFacet::isFromIllustration).collect(Collectors.toSet());
            result.getPublishingPlaces().addAll(publishingPlaces);

            Set<SubjectPlaceFacet> subjectPlaces = record.getSubjectPlaces().stream().filter(SubjectPlaceFacet::isFromIllustration).collect(Collectors.toSet());
            result.getSubjectPlaces().addAll(subjectPlaces);

            Set<SubjectPersonFacet> subjectPeople = record.getSubjectPersons().stream().filter(SubjectPersonFacet::isFromIllustration).collect(Collectors.toSet());
            result.getSubjectPersons().addAll(subjectPeople);

            Set<SubjectEntryFacet> subjectEntries = record.getSubjectEntries().stream().filter(SubjectEntryFacet::isFromIllustration).collect(Collectors.toSet());
            result.getSubjectEntries().addAll(subjectEntries);

            Set<Keyword> keywords = new HashSet<>(record.getKeywords());
            result.getKeywords().addAll(keywords);

            Set<Genre> genres = new HashSet<>(record.getGenres());
            result.getGenres().addAll(genres);
        }
        return result;
    }

    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }
}
