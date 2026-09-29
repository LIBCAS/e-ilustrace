package cz.inqool.eas.common.sequence;

import cz.inqool.eas.common.dictionary.DictionaryRepository;
import cz.inqool.eas.common.dictionary.index.DictionaryIndex;
import cz.inqool.eas.common.dictionary.store.DictionaryStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import static cz.inqool.eas.common.module.Modules.SEQUENCES;

public class SequenceRepository extends DictionaryRepository<
        Sequence,
        Sequence,
        SequenceIndexedObject,
        DictionaryStore<Sequence, Sequence, QSequence>,
        DictionaryIndex<Sequence, Sequence, SequenceIndexedObject>> {

    @Override
    protected ModuleDefinition getModule() {
        return SEQUENCES;
    }
}
