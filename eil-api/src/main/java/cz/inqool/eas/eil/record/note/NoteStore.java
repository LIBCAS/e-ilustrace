package cz.inqool.eas.eil.record.note;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class NoteStore extends DomainStore<Note, Note, QNote> {

    public NoteStore() {
        super(Note.class);
    }
}
