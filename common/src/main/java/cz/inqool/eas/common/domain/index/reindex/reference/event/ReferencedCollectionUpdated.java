package cz.inqool.eas.common.domain.index.reindex.reference.event;

import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * Register update/delete operation on an domain object collection for index reference synchronization.
 */
@Getter
public class ReferencedCollectionUpdated<C extends Collection<? extends Domain<?>>> extends ReferenceUpdated<C> {

    public ReferencedCollectionUpdated(@NonNull Object source, @NonNull C value) {
        this(source, value, null);
    }

    public ReferencedCollectionUpdated(@NonNull Object source, @NonNull C value, @Nullable Set<String> updatedFields) {
        super(source, value, updatedFields);
    }
}
