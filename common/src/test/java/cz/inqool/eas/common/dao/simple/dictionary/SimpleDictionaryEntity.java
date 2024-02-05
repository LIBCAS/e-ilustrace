package cz.inqool.eas.common.dao.simple.dictionary;

import cz.inqool.eas.common.dictionary.store.DictionaryObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Simple test entity extending the {@link DictionaryObject}
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
@Getter
@Setter
@Entity
@Table(name = "simple_dictionary_entity")
@FieldNameConstants
public class SimpleDictionaryEntity extends DictionaryObject<SimpleDictionaryEntity> {

    private String otherUselessValue;
}
