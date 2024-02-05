package cz.inqool.eas.eil.exhibition;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class ExhibitionRepository extends DatedRepository<
        Exhibition,
        Exhibition,
        ExhibitionIndexedObject,
        DatedStore<Exhibition, Exhibition, QExhibition>,
        DatedIndex<Exhibition, Exhibition, ExhibitionIndexedObject>> {

    public ExhibitionEssential findEssential(String id) {
        QExhibitionEssential model = QExhibitionEssential.exhibitionEssential;

        ExhibitionEssential obj = query()
                .select(model)
                .from(model)
                .where(model.id.eq(id))
                .where(model.deleted.isNull())
                .fetchFirst();

        detachAll();

        return obj;
    }
}
