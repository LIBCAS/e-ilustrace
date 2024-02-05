package cz.inqool.entityviews;


import java.lang.annotation.*;

/**
 * Maps views to specified views in other side of either RELATIONSHIP or INHERITANCE.
 * <p>
 * If @ViewableMapping is used on a class, then it specifies an INHERITANCE mapping.
 * In such case, class MUST extend some abstract superclass.
 * <p>
 * You should add @ViewableMapping for all declared views on a particular class,
 * otherwise declared views will extend a basic real superclass instead of a view of a superclass.
 * <p>
 * If @ViewableMapping is used on an attribute, then it specifies a RELATIONSHIP mapping.
 * <p>
 * You need to provide @ViewableProperty with declared views for which you want to use a @ViewableMapping.
 * Without it, the generator would use a basic entity despite having a @ViewableMapping on an attribute.
 * <p>
 * First example (INHERITANCE mapping):
 * <pre>
 *     &#64;ViewableClass(views = {"first", "second"})
 *     &#64;ViewableMapping(views = {"first"}, mappedTo = "green")
 *     &#64;ViewableMapping(views = {"second"}, mappedTo = "red")
 *     &#64;Entity
 *     public class Foo extends AbstractBar {...}
 * </pre>
 * <p>
 * The generator will create two hibernate view classes:
 * <pre>
 *     &#64;Entity
 *     public class FooFirst extends AbstractBarGreen implements View {...}
 * </pre>
 * <pre>
 *     &#64;Entity
 *     public class FooSecond  extends AbstractBarRed implements View {...}
 * </pre>
 * <p>
 * Second example (RELATIONSHIP mapping):
 * <pre>
 *     &#64;ViewableClass(views = {"red"})
 *     &#64;Entity
 *     public class Bar {...}
 *
 *     &#64;ViewableClass(views = {"liquid"})
 *     &#64;Entity
 *     public class Zet {
 *         protected String id;
 *         &#64;OneToOne
 *         protected Foo foo;
 *     }
 *
 *     &#64;ViewableClass(views = {"first", "second", "third"})
 *     &#64;Entity
 *     public class Foo {
 *         protected String id;
 *
 *         &#64;ViewableProperty(views = {"first", "second"}) // necessary
 *         &#64;ViewableMapping(views = {"first"}, mappedTo = "red")
 *         &#64;ViewableMapping(views = {"second"}, userRef = true)
 *         &#64;OneToOne
 *         protected Bar bar;
 *
 *         &#64;ViewableProperty(views = {"third"}) // necessary
 *         &#64;ViewableMapping(views = {"third"}, mappedTo = "liquid", useOneWay = true)
 *         &#64;OneToOne(mappedBy = "foo")
 *         protected Zet zet;
 *         }
 * <p>
 * The generator will create three hibernate view classes with various types for attributes:
 * <pre>
 *     &#64;Entity
 *     public class FooFirst implements View {
 *         protected String id;
 *
 *         &#64;OneToOne
 *         protected BarRed bar;
 *     }
 * </pre>
 * <pre>
 *     &#64;Entity
 *     public class FooSecond implements View {
 *         protected String id;
 *
 *         &#64;AttributeOverride(name = "id", column = @Column(name = "bar_id"))
 *         &#64;Embedded
 *         protected BarRef bar;
 *     }
 * </pre>
 * <pre>
 *     &#64;Entity
 *     public class FooThird implements View {
 *         protected String id;
 *
 *         &#64;OneToOne
 *         &#64;JoinColumn(name = "foo_id")
 *         protected ZetLiquid zet;
 *     }
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ViewableMappings.class)
public @interface ViewableMapping {
    String SKIP = "INTERNAL_SKIP";

    /**
     * Specifies the views for which the annotation will be applied.
     * <p>
     * View names have to be defined also with {@link ViewableClass} annotation for the class.
     * <p>
     * For RELATIONSHIP mappings, view names have to defined also with {@link ViewableAnnotation} annotation for the
     * attribute.
     */
    String[] views();

    /**
     * Specify the view of target entity used in mapping in generated view. Defaults to annotated property type.
     * <p>
     * Works for RELATIONSHIP && INHERITANCE mappings.
     */
    String mappedTo() default "";

    /**
     * Specify whether the view will use a special 'reference' view for an attribute.
     * <p>
     * Works for RELATIONSHIP mappings.
     */
    boolean useRef() default false;

    /**
     * Specify whether bidirectional relationship should be shifted to a one-directional relationship in the view.
     * <p>
     * Removes a "mappedBy = ..." in multiplicity association annotation
     * and adds a relevant @JoinColumn annotation.
     * <p>
     * Works for RELATIONSHIP mappings.
     */
    boolean useOneWay() default false;
}
