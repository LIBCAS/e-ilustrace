package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.author.Author;
import cz.inqool.eas.eil.author.AuthorRepository;
import cz.inqool.eas.eil.author.record.RecordAuthor;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.eas.eil.record.book.Book;
import gov.loc.marc21.slim.DataFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static cz.inqool.eas.eil.xml.mapper.Constants.*;
import static cz.inqool.eas.eil.xml.mapper.Utils.*;

/**
 * {@link Record#getCoauthors()}
 */
@Component
@Order(57)
public class DataFieldMapper700 implements DataFieldMapper {

    private AuthorRepository authorRepository;

    @Override
    public void toAttribute(DataFieldType field, Record record) {
        Author data = parseAuthorAttributes(field);
        Author author = findAuthor(data.getFullName(), data.getBirthYear(), data.getDeathYear());
        author = author == null ? createAuthor(data) : updateAuthor(field, author);
        boolean isFromBook = record instanceof Book;
        record.getAuthors().add(createRecordCoauthor(field, author, isFromBook));
    }

    private Author findAuthor(String fullName, String birthYear, String deathYear) {
        List<Author> authors = authorRepository.findByFullName(fullName);
        if (authors.isEmpty()) {
            return null;
        }
        if (authors.size() == 1) {
            return authors.get(0);
        } else {
            // more authors with given name were found -> find the right one using birth and death year
            return authorRepository.findByFullNameAndYears(fullName, birthYear, deathYear);
        }
    }

    private Author createAuthor(Author author) {
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        return authorRepository.create(author);
    }

    private Author updateAuthor(DataFieldType field, Author author) {
        // TODO napojeni na autority - ziskani a setovani jmennych variant (asynchronni joby?)
        author = updateAuthorAttributes(field, author);
        return authorRepository.update(author);
    }

    private static RecordAuthor createRecordCoauthor(DataFieldType field, Author author, boolean isFromBook) {
        RecordAuthor recordAuthor = new RecordAuthor();
        recordAuthor.setAuthor(author);
        recordAuthor.getRoles().addAll(mapXmlRolesToMarcRoles(field));
        recordAuthor.setMainAuthor(false);
        recordAuthor.setFromBook(isFromBook);
        recordAuthor.setFromIllustration(!isFromBook);
        return recordAuthor;
    }

    @Override
    public String getTag() {
        return TAG_700;
    }

    @Autowired
    public void setAuthorRepository(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
}
