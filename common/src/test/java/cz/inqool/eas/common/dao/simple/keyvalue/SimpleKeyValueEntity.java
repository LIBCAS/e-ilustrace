package cz.inqool.eas.common.dao.simple.keyvalue;

import cz.inqool.eas.common.domain.store.DomainObject;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity extending {@link DomainObject} for testing purposes
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "simple_key_value_entity")
@FieldNameConstants
public class SimpleKeyValueEntity extends DomainObject<SimpleKeyValueEntity> {

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
