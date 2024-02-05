package cz.inqool.eas.common.keyvalue;

import cz.inqool.eas.common.domain.store.DomainObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Used to store various simple string-string config pairs.
 * @author Lukas Jane (inQool) 23.04.2018.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@BatchSize(size = 100)
@Table(name = "eas_key_value")
public class KeyValue extends DomainObject<KeyValue> {
    private String key;
    private String value;

    /**
     * Use enums implementing this interface to specify used keys
     */
    public interface Key {
        String name();
    }
}
