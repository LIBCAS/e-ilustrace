package cz.inqool.eas.eil.domain;

import cz.inqool.eas.common.domain.index.reference.Labeled;

/**
 * Represents an enum class which can be used in ElasticSearch.
 */
public interface LabeledEnum<E extends Enum<E>> extends Labeled {

    /**
     * Returns the name of this enum constant.
     *
     * @see Enum#name()
     */
    String name();

    /**
     * Returns the user readable name (label) of this entity.
     *
     * @return the label of this entity
     */
    String getLabel();


    @Override
    default String getId() {
        return name();
    }
}
