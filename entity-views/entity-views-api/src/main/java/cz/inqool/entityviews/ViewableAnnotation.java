package cz.inqool.entityviews;

import java.lang.annotation.*;

/**
 * Restrict the set of annotations placed on generated views by configuring the default generator behaviour.
 * <p>
 * By default, all annotations of base class will be placed on the generated view.
 * <p>
 * This annotation can restrict views to place specific annotations only in a particular views.
 * <p>
 * Example:
 * <pre>
 *     &#64;ViewableClass(views = {"first", "second", "third"})
 *     &#64;ViewableAnnotation(views = {"second"}, value = {Entity.class})
 *     &#64;ViewableAnnotation(views = {"third"}, value = {Immutable.class})
 *     &#64;Entity
 *     public class Foo {...}
 * </pre>
 * <p>
 * The generator will create three hibernate view classes:
 * <pre>
 *     public class FooFirst implements View {...}
 * </pre>
 * <pre>
 *     &#64;Entity
 *     public class FooSecond implements View {...}
 * </pre>
 * <pre>
 *     // @Immutable will not be part of view
 *     // Because it was not on the original class
 *     // &#64;ViewableAnnotation only restricts views, it cannot add a new annotation
 *     public class FooThird implements View {...}
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ViewableAnnotations.class)
public @interface ViewableAnnotation {

    /**
     * Specifies the views for which the annotation will be applied (the view name has to be defined also with {@link
     * ViewableClass}) annotation.
     */
    String[] views() default {};

    /**
     * Specifies annotations placed on generated views (configured via {@link #views()}). The annotated (base) class
     * must be annotated with all specified annotations.
     */
    Class<?>[] value();
}
