package cz.inqool.eas.eil.exhibition.item;

import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class ExhibitionItemRepository extends DatedStore<ExhibitionItem, ExhibitionItem, QExhibitionItem> {

    public ExhibitionItemRepository() {
        super(ExhibitionItem.class);
    }
}
