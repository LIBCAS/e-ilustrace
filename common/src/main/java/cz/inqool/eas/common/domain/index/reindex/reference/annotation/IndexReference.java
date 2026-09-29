package cz.inqool.eas.common.domain.index.reindex.reference.annotation;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.field.IndexFieldNode;
import cz.inqool.eas.common.domain.index.reference.CollectionReference;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import cz.inqool.eas.common.domain.index.reindex.reference.IndexReferenceScanner;

import java.lang.annotation.*;

/**
 * Specifies a reference to a different entity in an indexed object class, so the updates in that entity can be
 * projected into this index.
 */
@Repeatable(IndexReferences.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexReference {

    /**
     * Type of entity referenced from current indexed object class.
     */
    Class<? extends Domain<?>> referencedClass();

    /**
     * Relative elasticsearch path to ID (must be indexed too) used to effectively search for affected records during
     * entity update. If left empty, default relative path will be computed for fields of type {@link DomainIndexed},
     * {@link CollectionReference} and {@link LabeledReference} - in these cases the absolute elasticsearch path to
     * annotated field appended with ".id" will be used. For other fields {@code idPath} must be provided otherwise an
     * exception will be thrown during reference scanning on application startup.
     *
     * @see IndexReferenceScanner#createIndexReferenceField(IndexReference, IndexFieldNode)
     */
    String idPath() default "";

    /**
     * Configure to reindex annotated field's entity only when particular field(s) of {@link #referencedClass()} is
     * changed
     */
    String[] onlyOnChanged() default {};
}

