package cz.inqool.eas.common.security.personal;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import cz.inqool.eas.common.sequence.QSequence;
import cz.inqool.eas.common.sequence.Sequence;
import cz.inqool.eas.common.sequence.SequenceIndexedObject;

public class PersonalEventRepository extends DatedRepository<
        PersonalEvent,
        PersonalEvent,
        PersonalEventIndexedObject,
        DatedStore<PersonalEvent, PersonalEvent, QPersonalEvent>,
        DatedIndex<PersonalEvent, PersonalEvent, PersonalEventIndexedObject>> {
}
