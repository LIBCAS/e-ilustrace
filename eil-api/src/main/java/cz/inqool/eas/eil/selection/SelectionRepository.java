package cz.inqool.eas.eil.selection;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public class SelectionRepository extends DatedRepository<
        Selection,
        Selection,
        SelectionIndexedObject,
        DatedStore<Selection, Selection, QSelection>,
        DatedIndex<Selection, Selection, SelectionIndexedObject>> {

    public SelectionUserFind findByUserId(@NotNull String id) {
        QSelectionUserFind model = QSelectionUserFind.selectionUserFind;

        SelectionUserFind result = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.user.id.eq(id))
                .fetchFirst();

        detachAll();
        return result;
    }

    public SelectionDetail findSelectionDetailByUserId(@NotNull String id) {
        QSelectionDetail model = QSelectionDetail.selectionDetail;

        var result = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.user.id.eq(id))
                .fetchFirst();

        detachAll();
        return result;
    }
}
