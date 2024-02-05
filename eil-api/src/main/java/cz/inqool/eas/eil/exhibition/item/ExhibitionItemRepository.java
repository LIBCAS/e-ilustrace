package cz.inqool.eas.eil.exhibition.item;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class ExhibitionItemRepository extends DatedRepository<
        ExhibitionItem,
        ExhibitionItem,
        ExhibitionItemIndexedObject,
        DatedStore<ExhibitionItem, ExhibitionItem, QExhibitionItem>,
        DatedIndex<ExhibitionItem, ExhibitionItem, ExhibitionItemIndexedObject>> {
}
