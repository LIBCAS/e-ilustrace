package cz.inqool.eas.common.domain.index.reference;

/**
 * Represents an entity class which can be used in ElasticSearch as a {@link LabeledReference}.
 */
public interface Labeled {

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
}
