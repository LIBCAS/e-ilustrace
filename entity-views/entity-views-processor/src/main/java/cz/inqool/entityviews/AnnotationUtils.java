package cz.inqool.entityviews;

import com.google.common.collect.Sets;
import com.sun.tools.javac.code.Symbol;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationUtils {
    public static <A extends Annotation> A getAnnotation(Element element, Class<A> annotationType) {
        A annotation = element.getAnnotation(annotationType);

        if (annotation != null) {
            return annotation;
        }

        return element.getAnnotationMirrors()
                .stream()
                .map(mirror -> {
                    Element annotationElement = mirror.getAnnotationType().asElement();
                    return annotationElement.getAnnotation(annotationType);
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A[] getAnnotations(Element element, Class<A> annotationType) {
        A[] localAnnotations = element.getAnnotationsByType(annotationType);

        Stream<A> nestedAnnotations = element.getAnnotationMirrors().
                stream().
                map(mirror -> {
                    Element annotationElement = mirror.getAnnotationType().asElement();
                    return annotationElement.getAnnotationsByType(annotationType);
                }).
                flatMap(Stream::of);

        return Stream.concat(Stream.of(localAnnotations), nestedAnnotations).toArray(num -> (A[]) Array.newInstance(annotationType, num));
    }

    public static Set<Symbol.ClassSymbol> getClassesAnnotatedWith(RoundEnvironment roundEnv, TypeElement a) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(a);

        Stream<Symbol.ClassSymbol> compoundStream = elements.stream().
                filter(ae -> ae.getKind() == ElementKind.ANNOTATION_TYPE).
                map(ae -> (Symbol.ClassSymbol) ae);


        TypeElement[] types = Stream.concat(Stream.of(a), compoundStream).toArray(TypeElement[]::new);

        return roundEnv.getElementsAnnotatedWithAny(types).
                stream().
                filter(ae -> ae.getKind() == ElementKind.CLASS).
                map(ae -> (Symbol.ClassSymbol) ae).
                collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getMarkingAnnotations(RoundEnvironment roundEnv) {
        Set<Class<? extends Annotation>> annotations = Sets.newHashSet(
                Viewable.class,
                ViewableClass.class,
                ViewableProperty.class,
                ViewableMapping.class,
                ViewableMappings.class,
                ViewableImplement.class,
                ViewableImplements.class,
                ViewableAnnotation.class,
                ViewableAnnotations.class,
                ViewableLeaf.class
        );

        Stream<String> primaryNames = annotations.
                stream().
                map(Class::getName);


        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWithAny(annotations);

        Stream<String> compoundNames = elements.stream().
                filter(ae -> ae.getKind() == ElementKind.ANNOTATION_TYPE).
                map(ae -> (Symbol.ClassSymbol) ae).
                map(ae -> ae.type.toString());


        return Stream.concat(primaryNames, compoundNames).collect(Collectors.toSet());
    }
}
