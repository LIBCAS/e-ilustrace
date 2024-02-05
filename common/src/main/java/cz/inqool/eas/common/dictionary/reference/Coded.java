package cz.inqool.eas.common.dictionary.reference;

/**
 * Represents an entity class which can be used in ElasticSearch as a {@link CodedReference}.
 */
public interface Coded {

    /**
     * Returns the ID of this entity.
     *
     * @return ID in form of UUID string
     */
    String getId();

    /**
     * Returns the user readable name (label) of this entity.
     *
     * @return the label of this entity
     */
    String getLabel();

    /**
     * Returns the user readable code of this entity.
     *
     * @return the label of this entity
     */
    String getCode();
}
