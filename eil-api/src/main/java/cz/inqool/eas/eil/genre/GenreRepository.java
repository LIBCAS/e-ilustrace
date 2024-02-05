package cz.inqool.eas.eil.genre;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class GenreRepository extends DatedRepository<
        Genre,
        Genre,
        GenreIndexedObject,
        DatedStore<Genre, Genre, QGenre>,
        DatedIndex<Genre, Genre, GenreIndexedObject>> {

    public Genre findByName(String name) {
        if (name == null) {
            return null;
        }
        QGenre model = QGenre.genre;

        Genre genre = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.name.eq(name))
                .fetchOne();

        detachAll();
        return genre;
    }
}
