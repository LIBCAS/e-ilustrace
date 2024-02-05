package cz.inqool.entityviews;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies hibernate view classes to be generated from this class.
 * <p>
 * All properties on annotated class MUST NOT be private so the generator can access them.
 * <p>
 * Generated views contains all properties of this class unless are restricted with {@link ViewableProperty}. Views will
 * be annotated with all annotations present on annotated class unless restricted with {@link ViewableAnnotation}.
 * <p>
 * Generated views also implement all interfaces of the annotated class, unless restricted by {@link ViewableImplement}
 * annotation. If the annotated class implements an interface with a generic type parameter of the annotated class, the
 * parameter will be replaced by the generated class type.
 * <p>
 * If the annotated class property is initialized with a default value, all generated views containing this property
 * will have it also initialized with the same value.
 * <p>
 * If the annotated class has defined a custom constructor(s), the generated view will also contain adequate
 * constructor(s).
 * <p>

 * First example (view implements Serializable interface):
 * <pre>
 *     &#64;ViewableClass(views = {"list"})
 *     &#64;Entity
 *     public class Operator implements Serializable {
 *         protected String id;
 *         protected String name;
 *     }
 * </pre>
 * will generate this view class:
 * <pre>
 *     &#64;Entity
 *     public class OperatorList implements View, Serializable {
 *         protected String id;
 *         protected String name;
 *     }
 * </pre>
 * <p>
 * Second example (only Detail view contains attribute 'age'):
 * <pre>
 *     &#64;ViewableClass(views = {"list", "detail"})
 *     &#64;Entity
 *     public class Operator {
 *         protected String id;
 *         protected String name;
 *         &#64;ViewableProperty(views = "detail")
 *         protected Integer age = 0;
 *     }
 * </pre>
 * will generate two view classes:
 * <pre>
 *     &#64;Entity
 *     public class OperatorDetail implements View {
 *         protected String id;
 *         protected String name;
 *         protected Integer age = 0;
 *     }
 * </pre>
 * and
 * <pre>
 *     &#64;Entity
 *     public class OperatorList implements View {
 *         protected String id;
 *         protected String name;
 *     }
 * </pre>
 * <p>
 * Third example (only reference view is generated):
 * <pre>
 *     &#64;ViewableClass(generateRef = true)
 *     &#64;Entity
 *     public class Operator {
 *         protected String id;
 *         protected String name;
 *         protected Integer age;
 *     }
 * </pre>
 * will generate this view class:
 * <pre>
 *     &#64;Entity
 *     public class OperatorRef implements View {
 *         protected String id;
 *     }
 * </pre>
 * Fourth example (view adapts to provided generic type):
 * <pre>
 *     public interface Bar&lt;T&gt; {...}
 * </pre>
 * <pre>
 *     &#64;ViewableClass(views = {"custom"})
 *     &#64;Entity
 *     public class Foo implements Bar&lt;Foo&gt; {...}
 * </pre>
 * <p>
 * The generator will create one hibernate view class:
 * <pre>
 *     &#64;Entity
 *     public class FooCustom implements View, Bar&lt;FooCustom&gt; {...}
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewableClass {

    /**
     * Set views to be generated from this class.
     * <p>
     * Provided values are used as the suffixes for generated view classes.
     * <p>
     * Suppose we have class:
     * <pre>
     *     &#64;ViewableClass(views = {"custom"})
     *     &#64;Entity
     *     public class Foo{...}
     * </pre>
     * <p>
     * The generator will create one hibernate view class:
     * <pre>
     *     &#64;Entity
     *     public class FooCustom implements View {...}
     * </pre>
     */
    String[] views() default {};

    /**
     * If set to {@code true}, also a special 'reference' view will be created
     * <p>
     * View with only one property - the ID of the entity
     */
    boolean generateRef() default false;
}
