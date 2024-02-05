package cz.inqool.eas.common.domain.index.reindex.reference.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * Register update/delete operation for index reference synchronization.
 */
@Getter
public abstract class ReferenceUpdated<T> extends ApplicationEvent {

    /**
     * Updated/deleted value
     */
    protected final T value;

    /**
     * Set of updated fields. Empty map = no field was updated, {@code null} value = all fields were updated.
     */
    protected final Set<String> updatedFields;


    public ReferenceUpdated(@NonNull Object source, T value, @Nullable Set<String> updatedFields) {
        super(source);
        this.value = value;
        this.updatedFields = updatedFields;
    }

    @Override
    public String toString() {
        return "ReferenceUpdated{" +
                "value=" + value +
                ", updatedFields=" + updatedFields +
                '}';
    }
}
