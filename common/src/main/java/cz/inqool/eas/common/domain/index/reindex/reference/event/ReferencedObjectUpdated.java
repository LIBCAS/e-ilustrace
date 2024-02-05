package cz.inqool.eas.common.domain.index.reindex.reference.event;

import cz.inqool.eas.common.domain.Domain;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * Register update/delete operation on an domain object for index reference synchronization.
 */
@Getter
public class ReferencedObjectUpdated<T extends Domain<?>> extends ReferenceUpdated<T> {

    public ReferencedObjectUpdated(@NonNull Object source, @NonNull T value) {
        this(source, value, null);
    }

    public ReferencedObjectUpdated(@NonNull Object source, @NonNull T value, @Nullable Set<String> updatedFields) {
        super(source, value, updatedFields);
    }
}
