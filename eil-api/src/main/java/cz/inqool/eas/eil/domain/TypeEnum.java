package cz.inqool.eas.eil.domain;

import cz.inqool.eas.common.domain.Domain;

/**
 * Represents an enum class which is used to describe subtypes of an entity, differentiated using discriminator.
 */
public interface TypeEnum<E extends Enum<E>, ROOT extends Domain<ROOT>> {

    /**
     * Returns discriminator column value for type.
     */
    String dtype();

    /**
     * Returns actual sub-type of {@link ROOT} represented by this type instance.
     */
    Class<? extends ROOT> type();
}
