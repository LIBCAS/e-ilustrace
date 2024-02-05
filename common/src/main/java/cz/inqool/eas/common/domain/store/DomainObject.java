package cz.inqool.eas.common.domain.store;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.projection.Projectable;
import cz.inqool.entityviews.ViewableAnnotation;
import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableImplement;
import cz.inqool.entityviews.ViewableProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Basic building block for every JPA entity with spport for entity views.
 * <p>
 * Defines attribute {@link DomainObject#id} of type {@link String}, which is initialized to a random {@link UUID} upon
 * creation.
 * <p>
 * Also implements {@link DomainObject#equals} and {@link DomainObject#hashCode()} based on {@link DomainObject#id}
 * equivalence and {@link DomainObject#toString()} method returning concrete class name with {@link DomainObject#id} to
 * easily log entity.
 *
 * Provides these views:
 * 1. default - full view with all fields
 * 2. create - empty view without id field
 * 3. update - empty view without id field
 *
 * @param <ROOT> Root of the projection type system
 */
@ViewableClass(views = {DEFAULT, CREATE, UPDATE, IDENTIFIED})
@ViewableImplement(value = Domain.class, views = {DEFAULT, IDENTIFIED})
@ViewableAnnotation(value = EqualsAndHashCode.class, views = {DEFAULT, IDENTIFIED})
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@MappedSuperclass
abstract public class DomainObject<ROOT> implements Domain<ROOT>, Projectable<ROOT> {
    /**
     * Id of the object.
     *
     * Needs to be public because of the
     */
    @ViewableProperty(views = {DEFAULT, IDENTIFIED})
    @Id
    @NotNull
    public String id = UUID.randomUUID().toString();

    @ViewableProperty(views = {DEFAULT, IDENTIFIED})
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + getId();
    }
}
