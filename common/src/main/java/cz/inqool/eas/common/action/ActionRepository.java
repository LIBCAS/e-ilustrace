package cz.inqool.eas.common.action;

import cz.inqool.eas.common.dictionary.DictionaryRepository;
import cz.inqool.eas.common.dictionary.index.DictionaryIndex;
import cz.inqool.eas.common.dictionary.store.DictionaryStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import static cz.inqool.eas.common.module.Modules.ACTIONS;

public class ActionRepository extends DictionaryRepository<
        Action,
        Action,
        ActionIndexedObject,
        DictionaryStore<Action, Action, QAction>,
        DictionaryIndex<Action, Action, ActionIndexedObject>> {

    @Override
    protected ModuleDefinition getModule() {
        return ACTIONS;
    }
}
