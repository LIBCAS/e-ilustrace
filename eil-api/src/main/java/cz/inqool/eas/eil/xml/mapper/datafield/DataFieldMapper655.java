package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.genre.Genre;
import cz.inqool.eas.eil.genre.GenreRepository;
import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;
import static cz.inqool.eas.eil.xml.mapper.Utils.getNameFromDataField;

/**
 * {@link Record#genres}
 */
@Component
@Order(54)
public class DataFieldMapper655 implements DataFieldMapper {

    private GenreRepository genreRepository;

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        String name = getNameFromDataField(field);
        Genre genre = genreRepository.findByName(name);
        genre = genre == null ? createGenre(field) : updateGenre(field, genre);
        record.getGenres().add(genre);
    }

    private Genre createGenre(DataFieldType field) {
        Genre genre = new Genre();
        field.getSubfield().forEach(subfield -> {
            switch (subfield.getCode()) {
                case CODE_A:
                    genre.setName(subfield.getValue());
                    break;
                case CODE_7:
                    genre.setAuthorityCode(subfield.getValue());
                    break;
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return genre;
    }

    private Genre updateGenre(DataFieldType field, Genre genre) {
        field.getSubfield().forEach(subfield -> {
            if (CODE_7.equals(subfield.getCode())) {
                genre.setAuthorityCode(subfield.getValue());
            }
        });
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return genre;
    }

    @Override
    public String getTag() {
        return TAG_655;
    }

    @Autowired
    public void setGenreRepository(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
}
