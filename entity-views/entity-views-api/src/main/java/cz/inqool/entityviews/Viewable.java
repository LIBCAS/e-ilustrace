package cz.inqool.entityviews;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to force generator to include a class in view generation.
 * <p>
 * Annotation is not needed when the class is annotated with any of the Viewable* annotations.
 * <p>
 * Typical use-case is when you use an additional personalised view declaration annotation.
 * For example: EAS/common submodule has annotations DomainViews and DictionaryViews.
 * <p>
 * These annotations declare some generic Viewable* annotations usage.
 * But since they are not part of the entity-views library then the generator does not know them.
 * Therefore, it is needed to force the generator to include the class into view generation
 * with this empty {@link Viewable} annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Viewable {
}
