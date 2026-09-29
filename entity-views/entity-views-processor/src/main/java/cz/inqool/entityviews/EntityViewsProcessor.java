package cz.inqool.entityviews;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import cz.inqool.entityviews.model.ClassModel;
import cz.inqool.entityviews.model.UnitModel;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;


@SupportedAnnotationTypes(
        {"cz.inqool.entityviews.ViewableClass", "cz.inqool.entityviews.Viewable"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class EntityViewsProcessor extends AbstractProcessor {
    private final ModelParser parser = new ModelParser();
    private final ViewGenerator generator = new ViewGenerator();

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        JavacProcessingEnvironment javacProcessingEnv = (JavacProcessingEnvironment) processingEnv;
        JavacElements elementUtils = javacProcessingEnv.getElementUtils();

        Trees trees = Trees.instance(javacProcessingEnv);

        generator.init(processingEnv);
        parser.init(elementUtils, trees, roundEnv);


        Set<Symbol.ClassSymbol> classes = annotations.stream()
                .flatMap(annotation -> AnnotationUtils.getClassesAnnotatedWith(roundEnv, annotation).stream())
                .collect(Collectors.toSet());

        UnitModel[] units = classes.
                stream().
                map(parser::parseClass).
                toArray(UnitModel[]::new);

        Arrays.stream(units).forEach(unit -> processUnit(unit, units));


        return true;
    }

    private void processUnit(UnitModel unit, UnitModel[] units) {
        ClassModel clazz = unit.getClazz();

        if (clazz.isGenerateRef()) {
            generator.generateRef(unit);
        }

        for (String view : clazz.getViews()) {
            generator.generateView(unit, view, units);
        }
    }
}
