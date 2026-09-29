package cz.inqool.eas.eil.author;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorRepository extends DatedRepository<
        Author,
        Author,
        AuthorIndexedObject,
        DatedStore<Author, Author, QAuthor>,
        DatedIndex<Author, Author, AuthorIndexedObject>> {

    public List<Author> findByFullName(String fullName) {
        QAuthor model = QAuthor.author;

        List<Author> author = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.fullName.eq(fullName))
                .fetch();

        detachAll();
        return author;
    }

    public Author findByFullNameAndYears(String fullName, String birthYear, String deathYear) {
        QAuthor model = QAuthor.author;

        Author author = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.fullName.eq(fullName))
                .where(model.birthYear.eq(birthYear))
                .where(model.deathYear.eq(deathYear))
                .fetchOne();

        detachAll();
        return author;
    }
}
