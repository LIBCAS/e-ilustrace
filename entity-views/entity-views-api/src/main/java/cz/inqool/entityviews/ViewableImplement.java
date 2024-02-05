package cz.inqool.entityviews;

import java.lang.annotation.*;

/**
 * Restrict the set of interfaces implemented on generated views by configuring the default generator behaviour.
 * <p>
 * By default, all interfaces of base class will be implemented by the generated view.
 * <p>
 * This annotation can restrict views to implement specific interfaces only in a particular views.
 * <p>
 * Example:
 * <pre>
 *     &#64;ViewableClass(views = {"first", "second", "third"})
 *     &#64;ViewableImplement(views = {"second"}, value = {Serializable.class})
 *     &#64;ViewableImplement(views = {"third"}, value = {Comparable.class})
 *     &#64;Entity
 *     public class Foo implements Serializable {...}
 * </pre>
 * <p>
 * The generator will create three hibernate view classes:
 * <pre>
 *     &#64;Entity
 *     public class FooFirst implements View {...}
 * </pre>
 * <pre>
 *     &#64;Entity
 *     public class FooSecond implements View, Serializable {...}
 * </pre>
 * <pre>
 *     // Comparable will not be part of view
 *     // Because it was not on the original class
 *     // &#64;ViewableImplement only restricts views,
 *     // it cannot add a new interface implementation
 *     &#64;Entity
 *     public class FooSecond implements View {...}
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewableImplements.class)
public @interface ViewableImplement {

    /**
     * Specifies the views for which the annotation will be applied (the view name has to be defined also with {@link
     * ViewableClass}) annotation.
     */
    String[] views() default {};

    /**
     * Specifies interfaces which the generated views (configured via {@link #views()}) will implement. The annotated
     * class must implement all the specified interfaces.
     */
    Class<?>[] value();
}
