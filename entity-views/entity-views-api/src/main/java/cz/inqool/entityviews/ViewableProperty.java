package cz.inqool.entityviews;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Include annotated property only in specified views by configuring the default generator behaviour
 * <p>
 * By default, all attributes of base class will be placed in the generated view.
 * <p>
 * Property can be either ATTRIBUTE or METHOD.
 * <p>
 * {@link ViewableProperty} is often used in conjugation with {@link ViewableMapping} annotation.
 * <p>
 * Another use-case, which is pretty tricky is the METHOD style. If the method has a name as {@code getXXX()}
 * then JacksonJson wil automatically convert the method to a json field (if the jackson is configured this way).
 * For example, you can have a {@code getName()} method that constructs a name for entity from multiple attributes.
 * The final JSON will contain a field {@code "name"}.
 * <p>
 * With {@link ViewableProperty} placed on such method and declaring "LIST" view you can easily create a new json field
 * that is available only during listing (when the LIST view is usually used).
 * <p>
 * Example:
 * <pre>
 *     &#64;ViewableClass(views = {"list", "detail"})
 *     &#64;Entity
 *     public class Operator {
 *         protected String id;
 *         protected String code;
 *
 *         &#64;ViewableProperty(views = "detail")
 *         protected Integer age;
 *
 *         &#64;ViewableProperty(views = "list")
 *         public String getName() { return code + "-" + id; }
 *     }
 * </pre>
 * will generate two view classes:
 * <pre>
 *     &#64;Entity
 *     public class OperatorDetail implements View {
 *         protected String id;
 *         protected String code;
 *         protected Integer age;
 *     }
 * </pre>
 * <pre>
 *     &#64;Entity
 *     public class OperatorList implements View {
 *         protected String id;
 *         protected String code;
 *
 *         // could be converted by jackson to a json field "name"
 *         public String getName() { return code + "-" + id; }
 *     }
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ViewableProperty {

    /**
     * Include annotated property only in specified views.
     */
    String[] views() default {};
}
