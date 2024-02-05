package cz.inqool.eas.common.dated.store;

import cz.inqool.eas.common.dated.Dated;
import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Building block for JPA entities, which want to track creation, update and delete times.
 * <p>
 * Provides attributes {@link DatedObject#created}, {@link DatedObject#updated}, {@link DatedObject#deleted}, which are
 * filled accordingly in {@link DatedStore}.
 * <p>
 * If used with {@link DatedStore} upon deleting an instance, the instance will not be deleted from database, instead
 * only marked as deleted by setting the {@link DatedObject#deleted} to non-null value.
 * <p>
 * {@link DatedObject#updated} wont be changed if no other change happened to the object!
 *
 * @param <ROOT> Root of the projection type system
 */
@ViewableClass(views = {DEFAULT, CREATE, UPDATE, IDENTIFIED})
@ViewableMapping(views = DEFAULT, mappedTo = DEFAULT)
@ViewableMapping(views = CREATE, mappedTo = CREATE)
@ViewableMapping(views = UPDATE, mappedTo = UPDATE)
@ViewableMapping(views = IDENTIFIED, mappedTo = IDENTIFIED)
@ViewableImplement(views = DEFAULT, value = Dated.class)
@ViewableAnnotation(views = "NONE", value = FieldNameConstants.class) // to exclude FieldNameConstants annotation from generated views
@Getter
@Setter
@FieldNameConstants(innerTypeName = "DatedFields")
@MappedSuperclass
abstract public class DatedObject<ROOT> extends DomainObject<ROOT> implements Dated<ROOT> {

    @ViewableProperty(views = {DEFAULT, IDENTIFIED})
    @Column(updatable = false, nullable = false)
    @GeneratorType(type = CreatedGenerator.class, when = GenerationTime.INSERT)
    protected Instant created;

    @ViewableProperty(views = DEFAULT)
    @GeneratorType(type = UpdatedGenerator.class, when = GenerationTime.ALWAYS)
    protected Instant updated;

    @ViewableProperty(views = {DEFAULT, IDENTIFIED})
    protected Instant deleted;
}