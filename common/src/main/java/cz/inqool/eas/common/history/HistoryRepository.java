package cz.inqool.eas.common.history;

import cz.inqool.eas.common.authored.AuthoredRepository;
import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import java.util.List;

import static cz.inqool.eas.common.module.Modules.HISTORY;

public class HistoryRepository extends AuthoredRepository<
        History,
        History,
        HistoryIndexedObject,
        AuthoredStore<History, History, QHistory>,
        AuthoredIndex<History, History, HistoryIndexedObject>> {
    /**
     * Retrieves all objects for specified entity.
     */
    public List<HistoryList> listFull(String entityId) {
        QHistoryList model = QHistoryList.historyList;

        List<HistoryList> list = query().
                select(model).
                from(model).
                where(model.deleted.isNull()).
                where(model.entityId.eq(entityId)).
                orderBy(model.created.desc()).
                fetch();

        detachAll();

        return list;
    }

    @Override
    protected ModuleDefinition getModule() {
        return HISTORY;
    }
}
